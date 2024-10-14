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
import top.mylove7.live.im.core.server.service.IMsgAckCheckService;
import top.mylove7.live.im.core.server.service.IRouterHandlerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ImAckConsumer.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private IMsgAckCheckService msgAckCheckService;
    @Resource
    private IRouterHandlerService routerHandlerService;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        //设置我们的namesrv地址
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        //声明消费组
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + ImAckConsumer.class.getSimpleName());
        //每次只拉取一条消息
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        mqPushConsumer.subscribe(ImCoreServerProviderTopicNames.JIUSHI_LIVE_IM_ACK_MSG_TOPIC, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            String json = new String(msgs.get(0).getBody());
            ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = JSON.parseObject(json, ImMsgBodyInTcpWsDto.class);
            Integer retryTimes = msgAckCheckService.getMsgAckTimes(imMsgBodyInTcpWsDto.getMsgId(), imMsgBodyInTcpWsDto.getUserId(), imMsgBodyInTcpWsDto.getAppId());
            log.info("重试次数 is {},msgId is {}", retryTimes, imMsgBodyInTcpWsDto.getMsgId());
            if (retryTimes == null || retryTimes == -1) {
                log.info("该条消息不存在，或者已过期，或者已经确认消费了{}",imMsgBodyInTcpWsDto.getMsgId());
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            //只支持1次重发
            if (retryTimes < 2) {
                msgAckCheckService.recordMsgAck(imMsgBodyInTcpWsDto, retryTimes + 1);
                msgAckCheckService.sendDelayMsg(imMsgBodyInTcpWsDto);
                routerHandlerService.sendMsgToClient(imMsgBodyInTcpWsDto);
            } else {
                msgAckCheckService.doMsgAck(imMsgBodyInTcpWsDto);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        log.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }


}
