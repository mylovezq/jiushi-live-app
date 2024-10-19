package top.mylove7.live.im.core.server.handler.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import top.mylove7.live.im.core.server.common.ImContextUtils;
import top.mylove7.live.im.core.server.handler.SimplyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 业务消息处理器
 *
 * @Author jiushi
 *
 * @Description
 */
@Component
@Slf4j
public class BizImMsgHandler implements SimplyHandler {


    @Resource
    private MQProducer mqProducer;

    @Override
    public void handler(ChannelHandlerContext ctx, ImTcpWsDto imTcpWsDto) {
        //前期的参数校验
        Long userId = ImContextUtils.getUserId(ctx);
        Long appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            log.error("attr error,imTcpWsDto is {}", imTcpWsDto);
            //有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }

        byte[] body = imTcpWsDto.getBody();
        if (body == null || body.length == 0) {
            log.error("body error,imTcpWsDto is {}", imTcpWsDto);
            return;
        }
        ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = JSONUtil.toBean(new String(body), ImMsgBodyInTcpWsDto.class);
        imMsgBodyInTcpWsDto.setFromMsgId(UUID.fastUUID().toString());
        imMsgBodyInTcpWsDto.setFromUserId(userId);
        Message message = new Message();
        message.setTopic(ImCoreServerProviderTopicNames.JIUSHI_LIVE_IM_BIZ_MSG_TOPIC);
        message.setBody(imMsgBodyInTcpWsDto.toByte());
        try {
            SendResult sendResult = mqProducer.send(message);
            log.info("[BizImMsgHandler]消息投递结果:{}", sendResult);
        } catch (Exception e) {
            log.error("send BizImMsgHandler error ,erros is :", e);
            throw new RuntimeException(e);
        }
    }
}
