package top.mylove7.live.api.gift.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.gift.constants.SendGiftTypeEnum;
import org.qiyu.live.gift.dto.GiftConfigDTO;
import org.qiyu.live.gift.interfaces.IGiftConfigRpc;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.mylove7.jiushi.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import top.mylove7.live.api.gift.service.IGiftService;
import top.mylove7.live.bank.dto.AccountTradeRespDTO;
import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.error.ApiErrorEnum;
import top.mylove7.live.api.gift.qo.GiftReqQo;
import top.mylove7.live.api.gift.vo.GiftConfigVO;
import top.mylove7.live.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.dto.SendGiftMq;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.error.ErrorAssert;
import top.mylove7.live.common.interfaces.topic.GiftProviderTopicNames;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.im.router.interfaces.constants.ImMsgBizCodeEnum;
import top.mylove7.live.im.router.interfaces.rpc.ImRouterRpc;
import top.mylove7.live.living.interfaces.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.dto.LivingRoomRespDTO;
import top.mylove7.live.living.interfaces.rpc.ILivingRoomRpc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author jiushi
 * @Description
 */
@Service
@Slf4j
public class GiftServiceImpl implements IGiftService {


    @DubboReference
    private IGiftConfigRpc giftConfigRpc;
    @DubboReference
    private ICurrencyAccountRpc currencyAccountRpc;
    @Resource
    private BankProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private StringRedisTemplate redisTemplate;
    @Resource
    private MQProducer mqProducer;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private ImRouterRpc routerRpc;
    private Cache<Integer, GiftConfigDTO> giftConfigDTOCache = Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(90, TimeUnit.SECONDS).build();

    // Lua 脚本
    String deductionOfBalance = "local cacheKey = KEYS[1]\n" +
            "local price = tonumber(ARGV[1])\n" +
            "local expireTime = tonumber(ARGV[2])\n" +
            "\n" +
            "-- 获取当前余额\n" +
            "local cacheBalance = redis.call('GET', cacheKey)\n" +
            "\n" +
            "if not cacheBalance then\n" +
            "    return '账户异常'\n" +
            "end\n" +
            "\n" +
            "cacheBalance = tonumber(cacheBalance)\n" +
            "\n" +
            "if cacheBalance == 0 or cacheBalance < price then\n" +
            "    return '余额不足'\n" +
            "end\n" +
            "\n" +
            "-- 扣减余额\n" +
            "redis.call('DECRBY', cacheKey, price)\n" +
            "-- 设置过期时间\n" +
            "redis.call('EXPIRE', cacheKey, expireTime)\n" +
            "\n" +
            "return '成功'";

    @Override
    public List<GiftConfigVO> listGift() {
        List<GiftConfigDTO> giftConfigDTOS = giftConfigRpc.queryGiftList();
        return ConvertBeanUtils.convertList(giftConfigDTOS, GiftConfigVO.class);
    }

    @Override
    public boolean send(GiftReqQo giftReqQo) {
        int giftId = giftReqQo.getGiftId();
        //map集合，判断本地是否有对象，如果有就返回，如果没有就rpc调用，同时注入到本地map中
        GiftConfigDTO giftConfigDTO = giftConfigDTOCache.get(giftId, id -> giftConfigRpc.getByGiftId(giftId));
        ErrorAssert.isNotNull(giftConfigDTO, ApiErrorEnum.GIFT_CONFIG_ERROR);
        ErrorAssert.isTure(!giftReqQo.getReceiverId().equals(giftReqQo.getSenderUserId()), ApiErrorEnum.NOT_SEND_TO_YOURSELF);

        String cacheKey = cacheKeyBuilder.buildUserBalance(JiushiLoginRequestContext.getUserId());
        DefaultRedisScript<String> deductionOfBalanceScript = new DefaultRedisScript<>(deductionOfBalance, String.class);
        // 执行 Lua 脚本
        String deductionOfBalanceResult = redisTemplate.execute(deductionOfBalanceScript, Arrays.asList(cacheKey), Arrays.asList(giftConfigDTO.getPrice(), 60 * 60 * 12));
        Assert.isTrue("成功".equals(deductionOfBalanceResult), deductionOfBalanceResult);

        SendGiftMq sendGiftMq = new SendGiftMq();
        Message message = this.buildMessage(giftReqQo, giftConfigDTO, sendGiftMq);
        try {
            //投递成功就只做扣减库存操作，就是失败，也从mq里面筛选出
            SendResult sendResult = mqProducer.send(message);
            log.info("送礼发送结果{}===发送内容{}", sendResult, sendGiftMq);

            //如果余额扣减成功
            Integer sendGiftType = sendGiftMq.getType();
            JSONObject jsonObject = new JSONObject();
            if (SendGiftTypeEnum.DEFAULT_SEND_GIFT.getCode().equals(sendGiftType)) {
                //触发礼物特效推送功能
                jsonObject.put("url", sendGiftMq.getUrl());
                LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
                reqDTO.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
                reqDTO.setRoomId(sendGiftMq.getRoomId());
                List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(reqDTO);
                this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS, jsonObject);
            } else if (SendGiftTypeEnum.PK_SEND_GIFT.getCode().equals(sendGiftType)) {
                //改成全直播间可见
                Long receiverId = sendGiftMq.getReceiverId();
               //this.pkImMsgSend(jsonObject, sendGiftMq, receiverId);
            }

            log.info("[gift-send] send result is {}", sendResult);
        } catch (Exception e) {
            log.info("[gift-send] send result is error:", e);
            //利用im将发送失败的消息告知用户
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "送礼失败");
            this.sendImMsgSingleton(sendGiftMq.getUserId(), ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode(), jsonObject);
            //回退redis
            return false;
        }
        return true;
    }

    private  Message buildMessage(GiftReqQo giftReqQo, GiftConfigDTO giftConfigDTO, SendGiftMq sendGiftMq) {
        sendGiftMq.setUserId(JiushiLoginRequestContext.getUserId());
        sendGiftMq.setGiftId(giftReqQo.getGiftId());
        sendGiftMq.setRoomId(giftReqQo.getRoomId());
        sendGiftMq.setReceiverId(giftReqQo.getReceiverId());
        sendGiftMq.setUrl(giftConfigDTO.getSvgaUrl());
        sendGiftMq.setType(giftReqQo.getType());
        sendGiftMq.setPrice(giftConfigDTO.getPrice());
        //避免重复消费
        sendGiftMq.setUuid(UUID.randomUUID().toString());
        Message message = new Message();
        message.setTopic(GiftProviderTopicNames.SEND_GIFT);
        message.setBody(JSON.toJSONBytes(sendGiftMq));
        return message;
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
            ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = new ImMsgBodyInTcpWsDto();
            imMsgBodyInTcpWsDto.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
            imMsgBodyInTcpWsDto.setBizCode(imMsgBizCodeEnum.getCode());
            imMsgBodyInTcpWsDto.setToUserId(userId);
            imMsgBodyInTcpWsDto.setData(jsonObject.toJSONString());
            return imMsgBodyInTcpWsDto;
        }).collect(Collectors.toList());
        routerRpc.batchSendMsg(imMsgBodies);
    }

    private void sendImMsgSingleton(Long userId, int bizCode, JSONObject jsonObject) {
        ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = new ImMsgBodyInTcpWsDto();
        imMsgBodyInTcpWsDto.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
        imMsgBodyInTcpWsDto.setBizCode(bizCode);
        imMsgBodyInTcpWsDto.setToUserId(userId);
        imMsgBodyInTcpWsDto.setData(jsonObject.toJSONString());
        routerRpc.sendMsg(imMsgBodyInTcpWsDto);
    }

//    private void pkImMsgSend(JSONObject jsonObject, SendGiftMq sendGiftMq, Long receiverId) {
//        //pk类型的送礼 要通知什么给直播间的用户
//        //url 礼物特效全直播间可见
//        //todo 进度条全直播间可见
//        // 1000,进度条长度一共是1000，每个礼物对于进度条的影响就是一个数值（500（A）：500（B），550：450）
//        // 直播pk进度是不是以roomId为维度，string，送礼（A）incr，送礼给（B）就是decr。
//        Long roomId = sendGiftMq.getRoomId();
//        String isOverCacheKey = cacheKeyBuilder.buildLivingPkIsOver(roomId);
//        if (redisTemplate.hasKey(isOverCacheKey)) {
//            return;
//        }
//        LivingRoomRespDTO respDTO = livingRoomRpc.queryByRoomId(roomId);
//        Long pkObjId = livingRoomRpc.queryOnlinePkUserId(roomId);
//        if (pkObjId == null || respDTO == null || respDTO.getAnchorId() == null) {
//            return;
//        }
//        Long pkUserId = respDTO.getAnchorId();
//        Long pkNum = 0L;
//        String pkNumKey = cacheKeyBuilder.buildLivingPkKey(roomId);
//        DefaultRedisScript<Long> redisScript = new DefaultRedisScript();
//        redisScript.setScriptText(LUA_SCRIPT);
//        redisScript.setResultType(Long.class);
//        Long sendGiftSeqNum = System.currentTimeMillis();
//        if (pkUserId.equals(receiverId)) {
//            Integer moveStep = sendGiftMq.getPrice() / 10;
//            pkNum = this.redisTemplate.execute(redisScript, Collections.singletonList(pkNumKey), PK_INIT_NUM, PK_MAX_NUM, PK_MIN_NUM, moveStep);
//            if (PK_MAX_NUM <= pkNum) {
//                jsonObject.put("winnerId", pkUserId);
//            }
//        } else if (pkObjId.equals(receiverId)) {
//            Integer moveStep = sendGiftMq.getPrice() / 10 * -1;
//            pkNum = this.redisTemplate.execute(redisScript, Collections.singletonList(pkNumKey), PK_INIT_NUM, PK_MAX_NUM, PK_MIN_NUM, moveStep);
//            if (PK_MIN_NUM >= pkNum) {
//                this.redisTemplate.opsForValue().set(cacheKeyBuilder.buildLivingPkIsOver(roomId), -1);
//                jsonObject.put("winnerId", pkObjId);
//            }
//        }
//        jsonObject.put("receiverId", sendGiftMq.getReceiverId());
//        jsonObject.put("sendGiftSeqNum", sendGiftSeqNum);
//        jsonObject.put("pkNum", pkNum);
//        jsonObject.put("url", sendGiftMq.getUrl());
//        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
//        livingRoomReqDTO.setRoomId(roomId);
//        livingRoomReqDTO.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
//        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
//        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_PK_SEND_GIFT_SUCCESS, jsonObject);
//    }
//

}
