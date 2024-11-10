package top.mylove7.live.msg.provider.consumer;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import top.mylove7.jiushi.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import top.mylove7.live.msg.provider.consumer.handler.MessageHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


/**
 * @Author jiushi
 * @Description
 */
@Component
@Slf4j
public class ImMsgConsumer implements InitializingBean {

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private MessageHandler singleMessageHandler;

    // 记录每个用户连接的im服务器地址，然后根据im服务器的连接地址去做具体机器的调用
    // 基于mq广播思路去做，可能会有消息风暴发生，100台im机器，99%的mq消息都是无效的，
    // 加入一个叫路由层的设计，router中转的设计，router就是一个dubbo的rpc层
    // A--》B im-core-server -> msg-provider(持久化) -> im-core-server -> 通知到b
    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        //老版本中会开启，新版本的mq不需要使用到
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + ImMsgConsumer.class.getSimpleName());
        //一次从broker中拉取10条消息到本地内存当中进行消费
        // 设置消费超时时间为10秒
        mqPushConsumer.setConsumeTimeout(10000);
        // 设置最大重试次数为3次
        mqPushConsumer.setMaxReconsumeTimes(3);
        mqPushConsumer.setConsumeMessageBatchMaxSize(10);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听im发送过来的业务消息topic
        mqPushConsumer.subscribe(ImCoreServerProviderTopicNames.JIUSHI_LIVE_IM_BIZ_MSG_TOPIC, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            log.info("mq业务消息触发接受");

            try {
                msgs.parallelStream().forEach(msg -> {
                    ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = JSON.parseObject(new String(msg.getBody()), ImMsgBodyInTcpWsDto.class);
                    singleMessageHandler.onMsgReceive(imMsgBodyInTcpWsDto);
                    log.info("mq业务消息触发接受处理成功");
                });
            } catch (Exception e) {
                log.error("mq业务消息发送失败", e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        log.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }
}
