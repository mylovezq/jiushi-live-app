package top.mylove7.live.im.core.server.consumer;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import top.mylove7.jiushi.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import top.mylove7.live.im.core.server.service.IRouterHandlerService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author jiushi
 *
 * @Description
 */
@Configuration
@Slf4j
public class ImAckConsumer implements InitializingBean {


    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private IRouterHandlerService routerHandlerService;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        // 设置消费超时时间为10秒
        mqPushConsumer.setConsumeTimeout(10000);
        // 设置最大重试次数为3次
        mqPushConsumer.setMaxReconsumeTimes(3);
        //设置我们的namesrv地址
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        //声明消费组
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + ImAckConsumer.class.getSimpleName());
        //每次只拉取一条消息
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        mqPushConsumer.subscribe(ImCoreServerProviderTopicNames.JIUSHI_LIVE_IM_ACK_MSG_TOPIC, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {

            try {
                //检查是否确认消费 或者 已达到最大重试次数
                String json = new String(msgs.get(0).getBody());
                ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = JSON.parseObject(json, ImMsgBodyInTcpWsDto.class);
                //如果没发送成功，说明 已经确认消费 或达到最大重试次数
                boolean sendSuccess =  routerHandlerService.sendMsgToClient(imMsgBodyInTcpWsDto);
                if (sendSuccess){
                    //说明重发信息成功，需要延迟再检查  直接rocket延迟一下
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (Exception e) {
                log.error("延迟检查im是否确认到达异常",e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        mqPushConsumer.start();
        log.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }



}
