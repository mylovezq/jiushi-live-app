package top.mylove7.live.living.provider.gift.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.mylove7.jiushi.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.enums.CommonStatusEum;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.topic.BalanceChangeTopic;
import top.mylove7.live.common.interfaces.utils.ListUtils;
import top.mylove7.live.living.interfaces.gift.constants.RedPacketStatusEum;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketConfigReqDTO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketReceiveDTO;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.room.rpc.ILivingRoomRpc;
import top.mylove7.live.living.provider.gift.dao.mapper.RedPacketConfigMapper;
import top.mylove7.live.living.provider.gift.dao.po.RedPacketConfigPO;
import top.mylove7.live.living.provider.gift.service.IRedPacketConfigService;
import top.mylove7.live.msg.constants.ImMsgBizCodeEnum;
import top.mylove7.live.msg.interfaces.ImMsgRouterRpc;
import top.mylove7.live.user.interfaces.bank.dto.BalanceMqDto;
import top.mylove7.live.user.interfaces.bank.interfaces.ICurrencyAccountRpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static top.mylove7.live.user.interfaces.bank.constants.TradeTypeEnum.RED_PACKET_RECHARGE;

/**
 * @Program: jiushi-live-app
 * @Description:
 * @Author: jiushi
 * @Create: 2024-08-12 16:50
 */
@Service
@Slf4j
public class RedPacketConfigServiceImpl implements IRedPacketConfigService {


    @Resource
    private RedPacketConfigMapper redPacketConfigMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;
    @DubboReference
    private ImMsgRouterRpc imMsgRouterRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private ICurrencyAccountRpc currencyAccountRpc;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public RedPacketConfigPO queryByAuthorId(Long authorId) {
        LambdaQueryWrapper<RedPacketConfigPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RedPacketConfigPO::getAnchorId, authorId);
        wrapper.eq(RedPacketConfigPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        wrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        wrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(wrapper);
    }

    @Override
    public RedPacketConfigPO queryByConfigCode(String code) {
        LambdaQueryWrapper<RedPacketConfigPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RedPacketConfigPO::getConfigCode, code);
        wrapper.eq(RedPacketConfigPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        wrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        wrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(wrapper);
    }

    @Override
    public boolean addOne(RedPacketConfigPO redPacketConfigPO) {
        redPacketConfigPO.setConfigCode(UUID.fastUUID().toString());
        return redPacketConfigMapper.insert(redPacketConfigPO) > 0;
    }

    @Override
    public boolean updateById(RedPacketConfigPO redPacketConfigPO) {
        return redPacketConfigMapper.updateById(redPacketConfigPO) > 0;
    }

    @Override
    public boolean prepareRedPacket(Long authorId) {
        RedPacketConfigPO configPO = this.queryByAuthorId(authorId);
        if (configPO == null) {
            return false;
        }
        String lockKey = cacheKeyBuilder.buildRedPacketInitLock();
        String code = configPO.getConfigCode();
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, code, 3, TimeUnit.SECONDS);
        if (!isLock) {
            return false;
        }
        Integer totalCount = configPO.getTotalCount();
        Long totalPrice = configPO.getTotalPrice();
        List<Long> redPacketPriceList = ListUtils.createRedPacketPriceList(totalCount, totalPrice);
        String cacheKey = cacheKeyBuilder.buildRedPacketList(code);
        List<List<Long>> splitList = ListUtils.splitList(redPacketPriceList, 100);
        for (List<Long> list : splitList) {
            redisTemplate.opsForList().leftPushAll(cacheKey, new ArrayList<>(list));
        }
        redisTemplate.expire(cacheKey, 1, TimeUnit.DAYS);
        configPO.setStatus(RedPacketStatusEum.IS_PREPARE.getCode());
        this.updateById(configPO);
        String prepareSuccessCacheKey = cacheKeyBuilder.buildRedPacketPrepareSuccessCache(code);
        redisTemplate.opsForValue().set(prepareSuccessCacheKey, 1, 1, TimeUnit.DAYS);
        return true;
    }

    @Override
    public boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        Long roomId = reqDTO.getRoomId();
        String code = reqDTO.getConfigCode();
        String cacheKey = cacheKeyBuilder.buildRedPacketPrepareSuccessCache(code);
        boolean isRedPacketPrepared = Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey));
        Assert.isTrue(isRedPacketPrepared, "请先初始化红包");

        String isRedPacketHadNotifyCacheKey = cacheKeyBuilder.buildRedPacketNotifyCache(code);
        boolean isRedPacketHadNotify = Boolean.TRUE.equals(redisTemplate.hasKey(isRedPacketHadNotifyCacheKey));
        Assert.isTrue(!isRedPacketHadNotify, "红包通知已发，请勿重复操作");
        RedPacketConfigPO configPO = this.queryByConfigCode(code);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("redPacketConfig", JSON.toJSONString(configPO));
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(roomId);
        livingRoomReqDTO.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
        if (CollectionUtils.isEmpty(userIdList)) {
            userIdList.add(reqDTO.getUserId());
            //return false;
        }
        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.START_RED_PACKET, jsonObject);
        configPO.setStatus(RedPacketStatusEum.HAD_SEND.getCode());
        this.updateById(configPO);
        redisTemplate.opsForValue().set(isRedPacketHadNotifyCacheKey, 1, 1, TimeUnit.DAYS);
        return true;
    }

    /**
     * 批量发送im消息
     *
     * @param userIdList
     * @param imMsgBizCodeEnum
     * @param jsonObject
     */
    private void batchSendImMsg(List<Long> userIdList, ImMsgBizCodeEnum imMsgBizCodeEnum, JSONObject jsonObject) {
        List<ImMsgBodyInTcpWsDto> imMsgBodies = userIdList.stream().map(userId -> {
            ImMsgBodyInTcpWsDto imMsgBody = new ImMsgBodyInTcpWsDto();
            imMsgBody.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
            imMsgBody.setBizCode(imMsgBizCodeEnum.getCode());
            imMsgBody.setToUserId(userId);
            imMsgBody.setMsgId(UUID.fastUUID().toString());
            imMsgBody.setData(jsonObject.toJSONString());
            return imMsgBody;
        }).collect(Collectors.toList());
        imMsgRouterRpc.batchSendMsg(imMsgBodies);
    }


    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        String code = redPacketConfigReqDTO.getConfigCode();
        String cacheKey = cacheKeyBuilder.buildRedPacketList(code);
        Object priceObj = redisTemplate.opsForList().rightPop(cacheKey);
        if (priceObj == null) {
            return null;
        }
        long priceObjLong = Long.parseLong(priceObj+"");
        String totalGetCacheKey = cacheKeyBuilder.buildRedPacketTotalGetCache(code);
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPacketTotalGetPriceCache(code);
        String userTotalGetPriceCacheKey = cacheKeyBuilder.buildUserTotalGetPriceCache(redPacketConfigReqDTO.getUserId());
        redisTemplate.opsForValue().increment(totalGetCacheKey);
        redisTemplate.expire(totalGetCacheKey, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().increment(totalGetPriceCacheKey,  priceObjLong);
        redisTemplate.expire(totalGetPriceCacheKey, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().increment(userTotalGetPriceCacheKey,  priceObjLong);

        //LUA记录最大值

        log.info("[用户{}收到code是{}的【{}】元红包]", redPacketConfigReqDTO.getUserId(), redPacketConfigReqDTO.getConfigCode(),redPacketConfigReqDTO.getTotalPrice());
        BalanceMqDto balanceMqDto = new BalanceMqDto();
        balanceMqDto.setUserId(redPacketConfigReqDTO.getUserId());
        balanceMqDto.setPrice(priceObjLong);
        balanceMqDto.setTradeType(RED_PACKET_RECHARGE.name());
        balanceMqDto.setTradeId(IdWorker.getId());

        SendResult sendResult = rocketMQTemplate.syncSend(BalanceChangeTopic.INCR_BALANCE, balanceMqDto);
        log.info("抢红包结果消息投递{}",sendResult);
        try {

            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                return new RedPacketReceiveDTO(balanceMqDto.getPrice(), "恭喜领取红包" + balanceMqDto.getPrice() + "九十币");
            }
        } catch (Exception e) {
            log.error("抢红包结果消息投递异常", e);
            throw new BizErrorException(e.getMessage());
        }
        return new RedPacketReceiveDTO(-1L, "抱歉，红包被人抢走了，再试试？");
    }

    @Override
    public void receiveRedPacketHandle(RedPacketConfigReqDTO reqDTO, Integer price) {
        String code = reqDTO.getConfigCode();
        String totalGetCacheKey = cacheKeyBuilder.buildRedPacketTotalGetCache(code);
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPacketTotalGetPriceCache(code);
        String userTotalGetPriceCacheKey = cacheKeyBuilder.buildUserTotalGetPriceCache(reqDTO.getUserId());
        redisTemplate.opsForValue().increment(totalGetCacheKey);
        redisTemplate.expire(totalGetCacheKey, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().increment(totalGetPriceCacheKey, price);
        redisTemplate.expire(totalGetPriceCacheKey, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().increment(userTotalGetPriceCacheKey, price);

        BalanceMqDto balanceMqDto
                = new BalanceMqDto(reqDTO.getUserId(), Long.valueOf(price), IdWorker.getId(), RED_PACKET_RECHARGE.name());
        currencyAccountRpc.incrBalance(balanceMqDto);
        redPacketConfigMapper.incrTotalGetPrice(code, price);
    }

}
