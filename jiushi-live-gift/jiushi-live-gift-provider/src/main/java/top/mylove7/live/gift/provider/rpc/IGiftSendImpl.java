package top.mylove7.live.gift.provider.rpc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
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
import org.qiyu.live.gift.interfaces.IGiftSendRpc;
import org.qiyu.live.gift.qo.GiftReqQo;
import top.mylove7.live.msg.constants.ImMsgBizCodeEnum;
import top.mylove7.live.msg.interfaces.ImRouterRpc;
import org.springframework.util.Assert;
import top.mylove7.live.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.dto.SendGiftMq;
import top.mylove7.live.common.interfaces.error.ApiErrorEnum;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.error.ErrorAssert;
import top.mylove7.live.common.interfaces.topic.GiftProviderTopicNames;
import top.mylove7.live.gift.provider.service.IGiftConfigService;

import top.mylove7.live.living.interfaces.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.dto.LivingRoomRespDTO;
import top.mylove7.live.living.interfaces.rpc.ILivingRoomRpc;

import java.util.List;
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
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(giftReqQo.getRoomId());
        giftReqQo.setReceiverId(livingRoomRespDTO.getId());
        GiftConfigDTO giftConfigDTO = giftConfigService.getByGiftId(giftId);
        ErrorAssert.isNotNull(giftConfigDTO, ApiErrorEnum.GIFT_CONFIG_ERROR);
        ErrorAssert.isTure(!giftReqQo.getReceiverId().equals(giftReqQo.getSenderUserId()), ApiErrorEnum.NOT_SEND_TO_YOURSELF);

        LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
        reqDTO.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
        reqDTO.setRoomId(giftReqQo.getRoomId());
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(reqDTO);
        log.info("直播间用户信息{}", userIdList);
        Assert.isTrue(CollUtil.isNotEmpty(userIdList),"直播间异常，请重进直播间");

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
            //没有走db，内部调用应该都很快
            this.sendGiftAfterDB(sendGiftMq);
        } catch (Exception e) {
            log.error("礼物特效推送功能异常", e);
            //直接抛给前端
            throw new BizErrorException("礼物发送异常");
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
            Assert.isTrue(CollUtil.isNotEmpty(userIdList),"直播间异常，请重进直播间");

            this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS, jsonObject);
        } else if (SendGiftTypeEnum.PK_SEND_GIFT.getCode().equals(sendGiftType)) {
            //改成全直播间可见
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
        sendGiftMq.setUuid(UUID.fastUUID().toString());
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
            imMsgBodyInTcpWsDto.setFromMsgId(UUID.fastUUID().toString());
            imMsgBodyInTcpWsDto.setData(jsonObject.toJSONString());
            return imMsgBodyInTcpWsDto;
        }).collect(Collectors.toList());
        routerRpc.batchSendMsg(imMsgBodies);
    }
    private void sendImMsgSingleton(Long userId, int bizCode, JSONObject jsonObject) {
        ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = new ImMsgBodyInTcpWsDto();
        imMsgBodyInTcpWsDto.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
        imMsgBodyInTcpWsDto.setBizCode(bizCode);
        imMsgBodyInTcpWsDto.setFromMsgId(UUID.fastUUID().toString());
        imMsgBodyInTcpWsDto.setToUserId(userId);
        imMsgBodyInTcpWsDto.setData(jsonObject.toJSONString());
        routerRpc.sendMsg(imMsgBodyInTcpWsDto);
    }
}
