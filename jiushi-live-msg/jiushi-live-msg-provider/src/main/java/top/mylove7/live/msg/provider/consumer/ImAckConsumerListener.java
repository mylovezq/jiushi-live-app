package top.mylove7.live.msg.provider.consumer;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.im.core.server.interfaces.rpc.IImCoreRouterHandlerRpc;

import static top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames.JIUSHI_LIVE_IM_ACK_MSG_TOPIC;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = JIUSHI_LIVE_IM_ACK_MSG_TOPIC,
        consumerGroup = "${spring.application.name}_ImAckConsumerListener",
        messageModel = MessageModel.CLUSTERING)
public class ImAckConsumerListener implements RocketMQListener<ImMsgBodyInTcpWsDto> {

    @Resource
    private IImCoreRouterHandlerRpc imCoreRouterHandler;


    /**
     * 消费消息的方法
     *
     * @param imMsgBodyInTcpWsDto 消息内容，类型和上面的泛型一致。如果泛型指定了固定的类型，消息体就是我们的参数
     */
    @Override
    public void onMessage(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {

        boolean sendSuccess = imCoreRouterHandler.sendMsgToClient(imMsgBodyInTcpWsDto);

        //能发送成功，说明之前没有发送给，判断辨识为true了  就发送了，发送了再发就检验 已经确认消费 或达到最大重试次数
        if (sendSuccess) {
           throw new BizErrorException("消息没有发送成功，开始进行校验");
        }
        log.info("消息确认送达");
    }


}
