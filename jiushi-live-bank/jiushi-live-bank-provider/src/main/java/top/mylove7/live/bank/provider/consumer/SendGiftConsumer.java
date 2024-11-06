package top.mylove7.live.bank.provider.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import top.mylove7.jiushi.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import top.mylove7.jiushi.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import top.mylove7.live.bank.provider.service.IMyCurrencyAccountService;
import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.dto.SendGiftMq;
import top.mylove7.live.common.interfaces.topic.GiftProviderTopicNames;
import top.mylove7.live.living.interfaces.room.rpc.ILivingRoomRpc;
import top.mylove7.live.msg.constants.ImMsgBizCodeEnum;
import top.mylove7.live.msg.interfaces.ImRouterRpc;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 发送礼物消费者
 *
 * @Author jiushi
 * @Description
 */
@Configuration
@Slf4j
public class SendGiftConsumer implements InitializingBean {




    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private BankProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private IMyCurrencyAccountService myCurrencyAccountService;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private ImRouterRpc routerRpc;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        //老版本中会开启，新版本的mq不需要使用到
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + SendGiftConsumer.class.getSimpleName());
        // 设置消费超时时间为10秒
        mqPushConsumer.setConsumeTimeout(10000);
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听礼物缓存数据更新的行为
        mqPushConsumer.subscribe(GiftProviderTopicNames.SEND_GIFT, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {

            MessageExt messageExt = msgs.get(0);

            SendGiftMq sendGiftMq = JSON.parseObject(new String(messageExt.getBody()), SendGiftMq.class);
            String mqConsumerKey = cacheKeyBuilder.buildGiftConsumeKey(sendGiftMq.getUuid());
            boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(mqConsumerKey, -1, 30, TimeUnit.MINUTES);
            if (!lockStatus) {
                //代表曾经消费过 并且成功过
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }

            try {
                log.error("开始扣减库存sendGiftMq{}",sendGiftMq);
                myCurrencyAccountService.decr(sendGiftMq.getUserId(),sendGiftMq.getPrice());
            } catch (Exception e) {
                log.error("扣减库存失败", e);
                redisTemplate.delete(mqConsumerKey);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;

            }
//            //如果余额扣减成功
//            Integer sendGiftType = sendGiftMq.getType();
//            JSONObject jsonObject = new JSONObject();
//            //改成全直播间可见
//            if (tradeRespDTO.isSuccess()) {
//                Long receiverId = sendGiftMq.getReceiverId();
//                if (SendGiftTypeEnum.DEFAULT_SEND_GIFT.getCode().equals(sendGiftType)) {
//                    //触发礼物特效推送功能
//                    jsonObject.put("url", sendGiftMq.getUrl());
//                    LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
//                    reqDTO.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
//                    reqDTO.setRoomId(sendGiftMq.getRoomId());
//                    List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(reqDTO);
//                    this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS, jsonObject);
//                } else if (SendGiftTypeEnum.PK_SEND_GIFT.getCode().equals(sendGiftType)) {
//                    this.pkImMsgSend(jsonObject, sendGiftMq, receiverId);
//                }
//            } else {
//                //利用im将发送失败的消息告知用户
//                jsonObject.put("msg", tradeRespDTO.getMsg());
//                this.sendImMsgSingleton(sendGiftMq.getUserId(), ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode(), jsonObject);
//            }
            log.info("[SendGiftConsumer] msg is {}", msgs);

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        log.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }


    /**
     * 单独发送im消息
     *
     * @param userId
     * @param bizCode
     * @param jsonObject
     */
    private void sendImMsgSingleton(Long userId, int bizCode, JSONObject jsonObject) {
        ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = new ImMsgBodyInTcpWsDto();
        imMsgBodyInTcpWsDto.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
        imMsgBodyInTcpWsDto.setBizCode(bizCode);
        imMsgBodyInTcpWsDto.setToUserId(userId);
        imMsgBodyInTcpWsDto.setData(jsonObject.toJSONString());
        routerRpc.sendMsg(imMsgBodyInTcpWsDto);
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
}
