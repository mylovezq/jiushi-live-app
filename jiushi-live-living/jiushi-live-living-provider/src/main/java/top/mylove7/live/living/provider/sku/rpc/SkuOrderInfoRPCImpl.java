package top.mylove7.live.living.provider.sku.rpc;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import top.mylove7.live.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.common.interfaces.topic.SkuProviderTopicNames;
import top.mylove7.live.living.interfaces.sku.constants.OrderStatusEnum;
import top.mylove7.live.living.interfaces.sku.dto.*;
import top.mylove7.live.living.interfaces.sku.rpc.ISkuOrderInfoRPC;
import top.mylove7.live.living.provider.sku.entity.SkuInfo;
import top.mylove7.live.living.provider.sku.entity.SkuOrderInfo;
import top.mylove7.live.living.provider.sku.service.IShopCarService;
import top.mylove7.live.living.provider.sku.service.ISkuInfoService;
import top.mylove7.live.living.provider.sku.service.ISkuOrderInfoService;
import top.mylove7.live.living.provider.sku.service.ISkuStockInfoService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Program: qiyu-live-app
 *
 * @Description:
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-16 13:53
 */
@DubboService
public class SkuOrderInfoRPCImpl implements ISkuOrderInfoRPC {

    private final Logger LOGGER = LoggerFactory.getLogger(SkuStockInfoRPCImpl.class);

    @Resource
    private ISkuOrderInfoService skuOrderInfoService;
    @Resource
    private IShopCarService shopCarService;
    @Resource
    private ISkuStockInfoService skuStockInfoService;
    @Resource
    private ISkuInfoService skuInfoService;
    @DubboReference
    private ICurrencyAccountRpc accountRpc;
    @Resource
    MQProducer mqProducer;


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
        skuOrderInfoReqDTO.setStatus((long) OrderStatusEnum.PREPARE_PAY.getCode());
        SkuOrderInfo skuOrderInfo = skuOrderInfoService.insertOne(skuOrderInfoReqDTO);
        if (skuOrderInfo == null) {
            return null;
        }
        shopCarService.removeFromCar(shopCarReqDTO);
        Long orderId = skuOrderInfo.getId();
        //库存回滚的mq延迟消息发送
        stockRollbackHandler(userId, orderId);

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
    public boolean payNow(PayNowReqDTO payNowReqDTO) {
        SkuOrderInfoRespDTO skuOrderInfoRespDTO = skuOrderInfoService.querySkuOrderInfo(payNowReqDTO.getUserId(), payNowReqDTO.getRoomId());
        if (OrderStatusEnum.PREPARE_PAY.getCode() != skuOrderInfoRespDTO.getStatus()) {
            LOGGER.error("payNow 订单状态为：{}，不是待支付状态", skuOrderInfoRespDTO.getStatus());
            return false;
        }
        List<Long> skuIdList = Arrays.stream(skuOrderInfoRespDTO.getSkuIdList().split(",")).toList().stream().map(Long::valueOf).toList();
        List<SkuInfo> skuInfoList = skuInfoService.queryBySkuIds(skuIdList);
        int num = 0;
        for (SkuInfo skuInfo : skuInfoList) {
            num += skuInfo.getSkuPrice();
        }
        Integer balance = accountRpc.getBalance(payNowReqDTO.getUserId());
        if (balance - num < 0) {
            LOGGER.error("payNow balance is no enough! balance = {}, num = {}", balance, num);
            return false;
        }

        SkuOrderInfoReqDTO skuOrderInfoReqDTO = new SkuOrderInfoReqDTO();
        skuOrderInfoReqDTO.setOrderId(skuOrderInfoRespDTO.getId());
        skuOrderInfoReqDTO.setStatus((long) OrderStatusEnum.PAYED.getCode());
        skuOrderInfoReqDTO.setUserId(skuOrderInfoRespDTO.getUserId());
        skuOrderInfoReqDTO.setRoomId(skuOrderInfoRespDTO.getRoomId());

        //扣减虚拟币
        accountRpc.decrByRedis(skuOrderInfoRespDTO.getUserId(), num);

        //更新订单状态
        boolean isSuccess = skuOrderInfoService.updateStatus(skuOrderInfoReqDTO);
        if (!isSuccess) {
            LOGGER.error("payNow skuOrderInfoService.updateStatus() isSuccess: {}", isSuccess);
            return false;
        }
        //删除缓存中的订单
        isSuccess = skuOrderInfoService.clearCacheOrder(skuOrderInfoReqDTO.getUserId(), skuOrderInfoReqDTO.getRoomId());
        if (!isSuccess) {
            LOGGER.error("payNow skuOrderInfoService.clearCacheOrder() isSuccess: {}", isSuccess);
        }
        //清空购物车
        ShopCarReqDTO shopCarReqDTO = new ShopCarReqDTO();
        shopCarReqDTO.setUserId(skuOrderInfoReqDTO.getUserId());
        shopCarReqDTO.setRoomId(skuOrderInfoReqDTO.getRoomId());
        isSuccess = shopCarService.clearCar(shopCarReqDTO);
        if (!isSuccess) {
            LOGGER.error("payNow shopCarService.clearCar() isSuccess: {}", isSuccess);
        }
        return true;
    }

    /**
     * 库存回滚的mq延迟消息发送
     * @param userId
     * @param orderId
     */
    private void stockRollbackHandler(Long userId, Long orderId) {
        Message message = new Message();
        message.setTopic(SkuProviderTopicNames.ROLL_BACK_STOCK);
        message.setBody(JSON.toJSONString(new RockBackInfoDTO(userId, orderId)).getBytes());
        //delayTimeLevel=1s 5s 10s(3) 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m(16) 1h 2h
        //从1开始 16 就是延迟 30m
        message.setDelayTimeLevel(16);
        try {
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
