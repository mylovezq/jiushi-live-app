package top.mylove7.live.living.provider.gift.rpc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.Assert;
import top.mylove7.jiushi.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.dto.SendGiftDto;
import top.mylove7.live.common.interfaces.error.ApiErrorEnum;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.error.ErrorAssert;
import top.mylove7.live.common.interfaces.topic.BalanceChangeTopic;
import top.mylove7.live.living.interfaces.gift.constants.SendGiftTypeEnum;
import top.mylove7.live.living.interfaces.gift.dto.GiftConfigDTO;
import top.mylove7.live.living.interfaces.gift.rpc.IGiftSendRpc;
import top.mylove7.live.living.interfaces.gift.qo.GiftReqQo;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomRespDTO;
import top.mylove7.live.living.interfaces.room.rpc.ILivingRoomRpc;
import top.mylove7.live.living.provider.gift.service.IGiftConfigService;
import top.mylove7.live.msg.constants.ImMsgBizCodeEnum;
import top.mylove7.live.msg.interfaces.ImMsgRouterRpc;
import top.mylove7.live.user.interfaces.bank.dto.BalanceMqDto;
import top.mylove7.live.user.interfaces.bank.interfaces.ICurrencyAccountRpc;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.rocketmq.client.producer.SendStatus.SEND_OK;

@DubboService
@Slf4j
public class IGiftSendImpl implements IGiftSendRpc {

    private static final Long PK_INIT_NUM = 50L;
    private static final Long PK_MAX_NUM = 100L;
    private static final Long PK_MIN_NUM = 0L;
    private String LUA_SCRIPT =
            "if (redis.call('exists', KEYS[1])) == 1 then " +
                    " local currentNum=redis.call('get',KEYS[1]) " +
                    " if (tonumber(currentNum)<=tonumber(ARGV[2]) and tonumber(currentNum)>=tonumber(ARGV[3])) then " +
                    " return redis.call('incrby',KEYS[1],tonumber(ARGV[4])) " +
                    " else return currentNum end " +
                    "else " +
                    "redis.call('set', KEYS[1], tonumber(ARGV[1])) " +
                    "redis.call('EXPIRE', KEYS[1], 3600 * 12) " +
                    "return ARGV[1] end";
    @Resource
    private IGiftConfigService giftConfigService;
    @DubboReference
    private ICurrencyAccountRpc currencyAccountRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private ImMsgRouterRpc routerRpc;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void sendGift(GiftReqQo giftReqQo) {
        int giftId = giftReqQo.getGiftId();
        GiftConfigDTO giftConfigDTO = giftConfigService.getByGiftId(giftId);
        ErrorAssert.isNotNull(giftConfigDTO, ApiErrorEnum.GIFT_CONFIG_ERROR);
        if (giftReqQo.getReceiverId() == null) {
            //为空默认送给直播间主播
            LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(giftReqQo.getRoomId());
            giftReqQo.setReceiverId(livingRoomRespDTO.getAnchorId());
            Assert.isTrue(!Objects.equals(livingRoomRespDTO.getAnchorId(), giftReqQo.getSenderUserId()), "不能给自己送礼");
        }

        LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
        reqDTO.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
        reqDTO.setRoomId(giftReqQo.getRoomId());
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(reqDTO);
        log.info("直播间用户信息{}", userIdList);
        Assert.isTrue(CollUtil.isNotEmpty(userIdList), "直播间异常，请重进直播间");

        currencyAccountRpc.decrBalanceByRedis(giftReqQo.getSenderUserId(), giftConfigDTO.getPrice());


        SendGiftDto sendGiftDto = this.buildMessage(giftReqQo, giftConfigDTO);
        try {
            //投递成功就只做扣减库存操作，就是失败，也从mq里面筛选出
            BalanceMqDto balanceMqDto = new BalanceMqDto();
            balanceMqDto.setTradeId(IdWorker.getId());
            balanceMqDto.setUserId(sendGiftDto.getUserId());
            balanceMqDto.setPrice(sendGiftDto.getPrice());
            SendResult sendResult = rocketMQTemplate.syncSend(BalanceChangeTopic.DECR_BALANCE, balanceMqDto);
            log.info("送礼发送{}结果===发送内容{}",sendResult, sendGiftDto);
            if (sendResult.getSendStatus() != SEND_OK){
                throw new BizErrorException("礼物发送失败");
            }

        } catch (Exception e) {
            log.info("送礼发送===发送异常", e);
            //人工兜底
            throw new BizErrorException("礼物发送异常");
        }

        try {
            //没有走db，内部调用应该都很快
            this.sendGiftAfterDB(sendGiftDto);
        } catch (Exception e) {
            log.error("礼物特效推送功能异常", e);
            //直接抛给前端
            throw new BizErrorException("礼物发送异常");
        }
    }

    private void sendGiftAfterDB(SendGiftDto sendGiftDto) {
        //如果余额扣减成功
        Integer sendGiftType = sendGiftDto.getType();
        JSONObject jsonObject = new JSONObject();
        if (SendGiftTypeEnum.DEFAULT_SEND_GIFT.getCode().equals(sendGiftType)) {
            //触发礼物特效推送功能
            this.sendDefaultGift(sendGiftDto, jsonObject);
            return;
        }
        if (SendGiftTypeEnum.PK_SEND_GIFT.getCode().equals(sendGiftType)) {
            //改成全直播间可见
            this.pkImMsgSend(jsonObject, sendGiftDto);
        }
    }

    private void sendDefaultGift(SendGiftDto sendGiftDto, JSONObject jsonObject) {
        jsonObject.put("url", sendGiftDto.getUrl());
        jsonObject.put("roomId", sendGiftDto.getRoomId());
        LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
        reqDTO.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
        reqDTO.setRoomId(sendGiftDto.getRoomId());
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(reqDTO);
        Assert.isTrue(CollUtil.isNotEmpty(userIdList), "直播间异常，请重进直播间");

        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS, jsonObject);
    }

    private void pkImMsgSend(JSONObject jsonObject, SendGiftDto sendGiftDto) {
        //pk类型的送礼 要通知什么给直播间的用户
        //url 礼物特效全直播间可见
        //todo 进度条全直播间可见
        // 1000,进度条长度一共是1000，每个礼物对于进度条的影响就是一个数值（500（A）：500（B），550：450）
        // 直播pk进度是不是以roomId为维度，string，送礼（A）incr，送礼给（B）就是decr。
        Long roomId = sendGiftDto.getRoomId();
        Long receiverId = sendGiftDto.getReceiverId();
        String isOverCacheKey = cacheKeyBuilder.buildLivingPkIsOver(roomId);
        if (redisTemplate.hasKey(isOverCacheKey)) {
            return;
        }
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByRoomId(roomId);
        Long pkObjId = livingRoomRpc.queryOnlinePkUserId(roomId);
        if (pkObjId == null || respDTO == null || respDTO.getAnchorId() == null) {
            return;
        }
        Long pkUserId = respDTO.getAnchorId();
        Long pkNum = 0L;
        String pkNumKey = cacheKeyBuilder.buildLivingPkKey(roomId);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript();
        redisScript.setScriptText(LUA_SCRIPT);
        redisScript.setResultType(Long.class);
        Long sendGiftSeqNum = System.currentTimeMillis();
        if (pkUserId.equals(receiverId)) {
            Long moveStep = sendGiftDto.getPrice() / 10;
            pkNum = this.redisTemplate.execute(redisScript, Collections.singletonList(pkNumKey), PK_INIT_NUM, PK_MAX_NUM, PK_MIN_NUM, moveStep);
            if (PK_MAX_NUM <= pkNum) {
                jsonObject.put("winnerId", pkUserId);
            }
        } else if (pkObjId.equals(receiverId)) {
            Long moveStep = sendGiftDto.getPrice() / 10 * -1;
            pkNum = this.redisTemplate.execute(redisScript, Collections.singletonList(pkNumKey), PK_INIT_NUM, PK_MAX_NUM, PK_MIN_NUM, moveStep);
            if (PK_MIN_NUM >= pkNum) {
                this.redisTemplate.opsForValue().set(cacheKeyBuilder.buildLivingPkIsOver(roomId), -1);
                jsonObject.put("winnerId", pkObjId);
            }
        }
        jsonObject.put("receiverId", sendGiftDto.getReceiverId());
        jsonObject.put("sendGiftSeqNum", sendGiftSeqNum);
        jsonObject.put("pkNum", pkNum);
        jsonObject.put("roomId", sendGiftDto.getRoomId());
        jsonObject.put("url", sendGiftDto.getUrl());
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(roomId);
        livingRoomReqDTO.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_PK_SEND_GIFT_SUCCESS, jsonObject);
    }

    private SendGiftDto buildMessage(GiftReqQo giftReqQo, GiftConfigDTO giftConfigDTO) {
        SendGiftDto sendGiftDto = new SendGiftDto();
        sendGiftDto.setUserId(giftReqQo.getSenderUserId());
        sendGiftDto.setGiftId(giftReqQo.getGiftId());
        sendGiftDto.setRoomId(giftReqQo.getRoomId());
        sendGiftDto.setReceiverId(giftReqQo.getReceiverId());
        sendGiftDto.setUrl(giftConfigDTO.getSvgaUrl());
        sendGiftDto.setType(giftReqQo.getType());
        sendGiftDto.setPrice(giftConfigDTO.getPrice());

        return sendGiftDto;
    }

    private void batchSendImMsg(List<Long> userIdList, ImMsgBizCodeEnum imMsgBizCodeEnum, JSONObject jsonObject) {
        List<ImMsgBodyInTcpWsDto> imMsgBodies = userIdList.parallelStream().map(userId -> {
            ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = new ImMsgBodyInTcpWsDto();
            imMsgBodyInTcpWsDto.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
            imMsgBodyInTcpWsDto.setBizCode(imMsgBizCodeEnum.getCode());
            imMsgBodyInTcpWsDto.setToUserId(userId);
            imMsgBodyInTcpWsDto.setMsgId(UUID.fastUUID().toString());
            imMsgBodyInTcpWsDto.setData(jsonObject.toJSONString());
            return imMsgBodyInTcpWsDto;
        }).collect(Collectors.toList());
        routerRpc.batchSendMsg(imMsgBodies);
    }

    private void sendImMsgSingleton(Long userId, int bizCode, JSONObject jsonObject) {
        ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = new ImMsgBodyInTcpWsDto();
        imMsgBodyInTcpWsDto.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
        imMsgBodyInTcpWsDto.setBizCode(bizCode);
        imMsgBodyInTcpWsDto.setMsgId(UUID.fastUUID().toString());
        imMsgBodyInTcpWsDto.setToUserId(userId);
        imMsgBodyInTcpWsDto.setData(jsonObject.toJSONString());
        routerRpc.sendMsg(imMsgBodyInTcpWsDto);
    }
}
