package top.mylove7.live.im.core.server.handler.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import top.mylove7.live.im.core.server.common.ImContextUtils;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.im.core.server.handler.SimplyHandler;

/**
 * 业务消息处理器
 *
 * @Author jiushi
 * @Description
 */
@Component
@Slf4j
public class BizImMsgHandler implements SimplyHandler {


    @Resource
    private RocketMQTemplate rocketMQTemplate;

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
        imMsgBodyInTcpWsDto.setMsgId(UUID.fastUUID().toString());
        imMsgBodyInTcpWsDto.setFromUserId(userId);
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(ImCoreServerProviderTopicNames.JIUSHI_LIVE_IM_BIZ_MSG_TOPIC, imMsgBodyInTcpWsDto);
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                log.error("投递im业务消息异常 :{}", sendResult);
                throw new BizErrorException("send BizImMsgHandler error");
            }
            log.info("投递im业务消息成功:{}", imMsgBodyInTcpWsDto);
        } catch (Exception e) {
            log.error("im业务消息发送失败：", e);
            throw new BizErrorException("im业务消息发送失败");
        }
    }
}
