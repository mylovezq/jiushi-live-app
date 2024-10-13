package top.mylove7.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import top.mylove7.live.common.interfaces.constants.ImMsgCodeEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;

import top.mylove7.live.im.core.server.common.ChannelHandlerContextCache;
import top.mylove7.live.im.core.server.common.ImContextUtils;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.im.core.server.handler.SimplyHandler;
import top.mylove7.live.common.interfaces.constants.ImCoreServerConstants;
import top.mylove7.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 登出消息的处理逻辑统一收拢到这个类中
 *
 * @Author jiushi
 *
 * @Description
 */
@Component
public class LogoutMsgHandler implements SimplyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutMsgHandler.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MQProducer mqProducer;

    @Override
    public void handler(ChannelHandlerContext ctx, ImTcpWsDto imTcpWsDto) {
        Long userId = ImContextUtils.getUserId(ctx);
        Long appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            LOGGER.error("attr error,imTcpWsDto is {}", imTcpWsDto);
            //有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        //将im消息回写给客户端
        logoutMsgNotice(ctx,userId,appId);
        logoutHandler(ctx, userId, appId);
    }

    /**
     * 登出的时候，发送确认信号，这个是正常网络断开才会发送，异常断线则不发送
     *
     * @param ctx
     * @param userId
     * @param appId
     */
    private void logoutMsgNotice(ChannelHandlerContext ctx, Long userId, Long appId) {
        ImMsgBodyInTcpWsDto respBody = new ImMsgBodyInTcpWsDto();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setData("true");
        ImTcpWsDto respMsg = ImTcpWsDto.build(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), JSON.toJSONString(respBody));
        ctx.writeAndFlush(respMsg);
        ctx.close();
    }

    /**
     * 登出的时候做缓存的清理和mq通知
     *
     * @param ctx
     * @param userId
     * @param appId
     */
    public void logoutHandler(ChannelHandlerContext ctx, Long userId, Long appId) {
        LOGGER.info("[LogoutMsgHandler] logout success,userId is {},appId is {}", userId, appId);
        //理想情况下，客户端断线的时候，会发送一个断线消息包
        ChannelHandlerContextCache.remove(userId);
        stringRedisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);
        ImContextUtils.removeUserId(ctx);
        ImContextUtils.removeAppId(ctx);
        this.sendLogoutMQ(ctx, userId, appId);
    }

    /**
     * 登出的时候发送mq消息
     *
     * @param ctx
     * @param userId
     * @param appId
     */
    public void sendLogoutMQ(ChannelHandlerContext ctx, Long userId, Long appId) {
        ImOfflineDTO imOfflineDTO = new ImOfflineDTO();
        imOfflineDTO.setUserId(userId);
        imOfflineDTO.setRoomId(ImContextUtils.getRoomId(ctx));
        imOfflineDTO.setAppId(appId);
        imOfflineDTO.setLoginTime(System.currentTimeMillis());
        Message message = new Message();
        message.setTopic(ImCoreServerProviderTopicNames.IM_OFFLINE_TOPIC);
        message.setBody(JSON.toJSONString(imOfflineDTO).getBytes());
        try {
            SendResult sendResult = mqProducer.send(message);
            LOGGER.error("[sendLogoutMQ] result im_offline_topic is {}", sendResult);
        } catch (Exception e) {
            LOGGER.error("[sendLogoutMQ] error im_offline_topic is: ", e);
        }
    }
}
