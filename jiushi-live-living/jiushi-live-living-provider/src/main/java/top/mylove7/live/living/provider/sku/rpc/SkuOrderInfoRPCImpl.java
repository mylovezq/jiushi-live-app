package top.mylove7.live.living.provider.sku.rpc;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;


import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.topic.SkuProviderTopicNames;

import top.mylove7.live.living.interfaces.sku.dto.*;
import top.mylove7.live.living.interfaces.sku.rpc.ISkuOrderInfoRPC;
import top.mylove7.live.living.provider.sku.entity.SkuInfo;
import top.mylove7.live.living.provider.sku.entity.SkuOrderInfo;
import top.mylove7.live.living.provider.sku.service.IShopCarService;
import top.mylove7.live.living.provider.sku.service.ISkuInfoService;
import top.mylove7.live.living.provider.sku.service.ISkuOrderInfoService;
import top.mylove7.live.living.provider.sku.service.ISkuStockInfoService;
import top.mylove7.live.user.interfaces.bank.constants.OrderStatusEnum;
import top.mylove7.live.user.interfaces.bank.dto.BalanceMqDto;
import top.mylove7.live.user.interfaces.bank.interfaces.ICurrencyAccountRpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static top.mylove7.live.user.interfaces.bank.constants.TradeTypeEnum.LIVING_ROOM_SHOP;

/**
 * @Program: jiushi-live-app
 * @Description:
 * @Author: tangfh
 * @Create: 2024-08-16 13:53
 */
@DubboService
@Slf4j
public class SkuOrderInfoRPCImpl implements ISkuOrderInfoRPC {

    @Resource
    private ISkuOrderInfoService skuOrderInfoService;
    @Resource
    private IShopCarService shopCarService;
    @Resource
    private ISkuStockInfoService skuStockInfoService;
    @Resource
    private ISkuInfoService skuInfoService;
    @DubboReference
    private ICurrencyAccountRpc currencyAccountRpc;
    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public SkuOrderInfoRespDTO querySkuOrderInfo(Long userId, Long roomId) {
        return skuOrderInfoService.querySkuOrderInfo(userId, roomId);
    }

    @Override
    public boolean insertOne(SkuOrderInfoReqDTO reqDTO) {
        return skuOrderInfoService.insertOne(reqDTO) != null;
    }

    @Override
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderReqDTO reqDTO) {
        ShopCarReqDTO shopCarReqDTO = new ShopCarReqDTO();
        Long userId = reqDTO.getUserId();
        shopCarReqDTO.setUserId(userId);
        shopCarReqDTO.setRoomId(reqDTO.getRoomId());
        ShopCarRespDTO shopCarRespDTO = shopCarService.getCarInfo(shopCarReqDTO);
        List<ShopCarItemRespDTO> shopCarItemRespDTOList = shopCarRespDTO.getShopCarItemRespDTOList();
        if (CollectionUtils.isEmpty(shopCarItemRespDTOList)) {
            return null;
        }
        List<Long> skuIdList = shopCarItemRespDTOList.stream().map(shopCarItemRespDTO -> shopCarItemRespDTO.getSkuInfoDTO().getSkuId()).toList();
        //核心的知识点库存回滚
        //10个skuId前5个扣减成功了，后边5个有问题
        boolean isDecrSuccess = skuStockInfoService.decrStockNumBySkuIdCache(skuIdList, 1);
        if (!isDecrSuccess) {
            return null;
        }
        //订单超时的概念，21：00,21：30分订单会自动关闭，21：25分的时候会有订单是醒功能
        //1.定时任务 扫描DB, 指定好索引，如果数据量非常高，扫描表的sqL会很耗时
        //2.redis的过期回调key过期之后，会有一个回调通知，ttl到期之后会回调到订阅方，回调并不是高可靠的，可能回丢失
        //3.rocketmq延迟消息，时间轮去做的，将扣减库存的信息利用下mg发送出去，在延迟回调处进行校验，
        SkuOrderInfoReqDTO skuOrderInfoReqDTO = new SkuOrderInfoReqDTO();
        skuOrderInfoReqDTO.setSkuIdList(skuIdList);
        skuOrderInfoReqDTO.setUserId(userId);
        skuOrderInfoReqDTO.setRoomId(reqDTO.getRoomId());
        skuOrderInfoReqDTO.setStatus(OrderStatusEnum.WAITING_PAY.getCode());
        SkuOrderInfo skuOrderInfo = skuOrderInfoService.insertOne(skuOrderInfoReqDTO);
        if (skuOrderInfo == null) {
            return null;
        }
        shopCarService.removeFromCar(shopCarReqDTO);
        Long orderId = skuOrderInfo.getId();
        //库存回滚的mq延迟消息发送
        this.stockRollbackHandler(userId, orderId);

        List<SkuPrepareOrderItemInfoDTO> skuPrepareOrderItemInfoDTOList = new ArrayList<>();
        int totalPrice = 0;
        for (ShopCarItemRespDTO shopCarItemRespDTO : shopCarItemRespDTOList) {
            SkuPrepareOrderItemInfoDTO skuPrepareOrderItemInfoDTO = new SkuPrepareOrderItemInfoDTO();
            skuPrepareOrderItemInfoDTO.setSkuInfoDTO(shopCarItemRespDTO.getSkuInfoDTO());
            skuPrepareOrderItemInfoDTO.setCount(shopCarItemRespDTO.getCount());
            totalPrice += shopCarItemRespDTO.getSkuInfoDTO().getSkuPrice();
            skuPrepareOrderItemInfoDTOList.add(skuPrepareOrderItemInfoDTO);
        }

        SkuPrepareOrderInfoDTO skuPrepareOrderInfoDTO = new SkuPrepareOrderInfoDTO();
        skuPrepareOrderInfoDTO.setTotalPrice(totalPrice);
        skuPrepareOrderInfoDTO.setSkuPrepareOrderItemInfoDTOList(skuPrepareOrderItemInfoDTOList);
        return skuPrepareOrderInfoDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //TODO 后续 投递消息 削峰 或者整个方法做异步mq
    public boolean payNow(PayNowReqDTO payNowReqDTO) {
        SkuOrderInfoRespDTO skuOrderInfoRespDTO = skuOrderInfoService.querySkuOrderInfo(payNowReqDTO.getUserId(), payNowReqDTO.getRoomId());

        List<Long> skuIdList = Arrays.stream(skuOrderInfoRespDTO.getSkuIdList().split(",")).toList().stream().map(Long::valueOf).toList();
        List<SkuInfo> skuInfoList = skuInfoService.queryBySkuIds(skuIdList);
        Long allSkuPrice = skuInfoList.parallelStream().map(SkuInfo::getSkuPrice).reduce(0L, Long::sum);

        //更新订单状态
        boolean updatePay = skuOrderInfoService.lambdaUpdate()
                .set(SkuOrderInfo::getStatus, OrderStatusEnum.PAYED.getCode())
                .eq(SkuOrderInfo::getUserId, skuOrderInfoRespDTO.getUserId())
                .eq(SkuOrderInfo::getStatus, OrderStatusEnum.WAITING_PAY.getCode())
                .update();
        if (!updatePay) {
            log.error("payNow skuOrderInfoService.updateStatus() isSuccess: {}", updatePay);
            throw new BizErrorException("订单状态发生变更，请刷新");
        }

        //删除缓存中的订单
        skuOrderInfoService.clearCacheOrder(skuOrderInfoRespDTO.getUserId(), skuOrderInfoRespDTO.getRoomId());
        //清空购物车
        ShopCarReqDTO shopCarReqDTO = new ShopCarReqDTO();
        shopCarReqDTO.setUserId(skuOrderInfoRespDTO.getUserId());
        shopCarReqDTO.setRoomId(skuOrderInfoRespDTO.getRoomId());
        shopCarService.clearCar(shopCarReqDTO);


        BalanceMqDto balanceMqDto = new BalanceMqDto();
        balanceMqDto.setTradeId(IdWorker.getId());
        balanceMqDto.setPrice(allSkuPrice);
        balanceMqDto.setTradeType(LIVING_ROOM_SHOP.name());
        balanceMqDto.setUserId(skuOrderInfoRespDTO.getUserId());

        //扣减虚拟币 当前可以用数据库承接 扣减失败就抛错   就事务回滚
        currencyAccountRpc.decrBalanceByDB(balanceMqDto);

        return true;
    }

    /**
     * 库存回滚的mq延迟消息发送
     *
     * @param userId
     * @param orderId
     */
    private void stockRollbackHandler(Long userId, Long orderId) {
        rocketMQTemplate.syncSend(SkuProviderTopicNames.ROLL_BACK_STOCK, new RockBackInfoDTO(userId, orderId));

    }
}
