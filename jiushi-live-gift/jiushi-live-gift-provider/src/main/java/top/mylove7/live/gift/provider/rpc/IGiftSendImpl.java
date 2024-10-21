package top.mylove7.live.gift.provider.rpc;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.gift.constants.SendGiftTypeEnum;
import org.qiyu.live.gift.dto.GiftConfigDTO;
import org.qiyu.live.gift.dto.GiftRecordDTO;
import org.qiyu.live.gift.interfaces.IGiftSendRpc;
import org.qiyu.live.gift.qo.GiftReqQo;
import top.mylove7.live.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.dto.SendGiftMq;
import top.mylove7.live.common.interfaces.error.ApiErrorEnum;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.error.ErrorAssert;
import top.mylove7.live.common.interfaces.topic.GiftProviderTopicNames;
import top.mylove7.live.gift.provider.service.IGiftConfigService;
import top.mylove7.live.im.router.interfaces.constants.ImMsgBizCodeEnum;
import top.mylove7.live.im.router.interfaces.rpc.ImRouterRpc;
import top.mylove7.live.living.interfaces.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.rpc.ILivingRoomRpc;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@DubboService
@Slf4j
public class IGiftSendImpl implements IGiftSendRpc {
    private Cache<Integer, GiftConfigDTO> giftConfigDTOCache = Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(90, TimeUnit.SECONDS).build();

    @Resource
    private IGiftConfigService giftConfigService;
    @DubboReference
    private ICurrencyAccountRpc currencyAccountRpc;
    @Resource
    private MQProducer mqProducer;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private ImRouterRpc routerRpc;
    @Override
    public void sendGift(GiftReqQo giftReqQo) {
        int giftId = giftReqQo.getGiftId();
        GiftConfigDTO giftConfigDTO = giftConfigService.getByGiftId(giftId);
        ErrorAssert.isNotNull(giftConfigDTO, ApiErrorEnum.GIFT_CONFIG_ERROR);
        ErrorAssert.isTure(!giftReqQo.getReceiverId().equals(giftReqQo.getSenderUserId()), ApiErrorEnum.NOT_SEND_TO_YOURSELF);

        currencyAccountRpc.decrByRedis(giftReqQo.getSenderUserId(), giftConfigDTO.getPrice());

        SendGiftMq sendGiftMq = new SendGiftMq();
        Message message = this.buildMessage(giftReqQo, giftConfigDTO, sendGiftMq);
        try {
            //投递成功就只做扣减库存操作，就是失败，也从mq里面筛选出
            SendResult sendResult = mqProducer.send(message);
            log.info("送礼发送结果{}===发送内容{}", sendResult, sendGiftMq);

        } catch (Exception e) {
            log.info("[gift-send] send result is error:", e);
            //人工兜底
            throw new BizErrorException("礼物发送失败");
        }

        try {
            this.sendGiftAfterDB(sendGiftMq);
        } catch (Exception e) {
            log.error("礼物特效推送功能异常", e);
            //利用im将发送失败的消息告知用户
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "送礼失败");
            this.sendImMsgSingleton(sendGiftMq.getUserId(), ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode(), jsonObject);
        }
    }

    private void sendGiftAfterDB(SendGiftMq sendGiftMq) {
        //如果余额扣减成功
        Integer sendGiftType = sendGiftMq.getType();
        JSONObject jsonObject = new JSONObject();
        if (SendGiftTypeEnum.DEFAULT_SEND_GIFT.getCode().equals(sendGiftType)) {
            //触发礼物特效推送功能
            jsonObject.put("url", sendGiftMq.getUrl());
            jsonObject.put("roomId", sendGiftMq.getRoomId());
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
    }

    private  Message buildMessage(GiftReqQo giftReqQo, GiftConfigDTO giftConfigDTO, SendGiftMq sendGiftMq) {
        sendGiftMq.setUserId(giftReqQo.getSenderUserId());
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

    private void batchSendImMsg(List<Long> userIdList, ImMsgBizCodeEnum imMsgBizCodeEnum, JSONObject jsonObject) {
        List<ImMsgBodyInTcpWsDto> imMsgBodies = userIdList.parallelStream().map(userId -> {
            ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = new ImMsgBodyInTcpWsDto();
            imMsgBodyInTcpWsDto.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
            imMsgBodyInTcpWsDto.setBizCode(imMsgBizCodeEnum.getCode());
            imMsgBodyInTcpWsDto.setToUserId(userId);
            imMsgBodyInTcpWsDto.setFromMsgId(cn.hutool.core.lang.UUID.fastUUID().toString());
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
}
