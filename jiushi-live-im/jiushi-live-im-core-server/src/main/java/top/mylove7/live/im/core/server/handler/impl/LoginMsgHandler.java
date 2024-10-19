package top.mylove7.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.account.interfaces.im.ImTokenRpc;
import top.mylove7.live.common.interfaces.dto.ImUserInfoTokenDto;
import top.mylove7.live.common.interfaces.constants.ImConstants;
import top.mylove7.live.common.interfaces.constants.ImMsgCodeEnum;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;

import top.mylove7.live.im.core.server.common.ChannelHandlerContextCache;
import top.mylove7.live.im.core.server.common.ImContextUtils;
import top.mylove7.live.im.core.server.handler.SimplyHandler;
import top.mylove7.live.common.interfaces.constants.ImCoreServerConstants;
import top.mylove7.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 登录消息的处理逻辑统一收拢到这个类中
 *
 * @Author jiushi
 *
 * @Description
 */
@Component
@Slf4j
public class LoginMsgHandler implements SimplyHandler {

    @DubboReference
    private ImTokenRpc imTokenRpc;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MQProducer mqProducer;

    @Override
    public void handler(ChannelHandlerContext ctx, ImTcpWsDto imTcpWsDto) {
        //防止重复请求
        if (ImContextUtils.getUserId(ctx) != null) {
            return;
        }
        byte[] body = imTcpWsDto.getBody();
        if (body == null || body.length == 0) {
            ctx.close();
            log.error("body error,imTcpWsDto is {}", imTcpWsDto);
            throw new IllegalArgumentException("body error");
        }
        ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = JSON.parseObject(new String(body), ImMsgBodyInTcpWsDto.class);
        Long userIdFromMsg = imMsgBodyInTcpWsDto.getToUserId();
        Long appId = imMsgBodyInTcpWsDto.getAppId();
        String token = imMsgBodyInTcpWsDto.getToken();
        if (StringUtils.isEmpty(token) || userIdFromMsg < 10000 || appId < 10000) {
            ctx.close();
            log.error("param error,imTcpWsDto is {}", imTcpWsDto);
            throw new IllegalArgumentException("param error");
        }
        ImUserInfoTokenDto userIdByToken = imTokenRpc.getUserIdByToken(token);
        Assert.notNull(userIdByToken, "token is null");
        //token校验成功，而且和传递过来的userId是同一个，则允许建立连接

         loginSuccessHandler(ctx, userIdByToken.getUserId(), userIdByToken.getAppId(), null);

        ctx.close();
        log.error("token check error,imTcpWsDto is {}", imTcpWsDto);
        throw new IllegalArgumentException("token check error");
    }

    /**
     * 用户登录的时候发送mq消息
     *
     * @param userId
     * @param appId
     */
    private void sendLoginMQ(Long userId, Long appId, Long roomId) {
        ImOnlineDTO imOnlineDTO = new ImOnlineDTO();
        imOnlineDTO.setUserId(userId);
        imOnlineDTO.setAppId(appId);
        imOnlineDTO.setRoomId(roomId);
        imOnlineDTO.setLoginTime(System.currentTimeMillis());
        Message message = new Message();
        message.setTopic(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC);
        message.setBody(JSON.toJSONString(imOnlineDTO).getBytes());
        try {
            SendResult sendResult = mqProducer.send(message);
            log.info("[sendLogin online MQ] sendResult is {}", sendResult);
        } catch (Exception e) {
            log.error("[sendLogin online MQ] error is: ", e);
        }
    }

    /**
     * 如果用户登录成功则处理相关记录
     *
     * @param ctx
     * @param userId
     * @param appId
     */
    public void loginSuccessHandler(ChannelHandlerContext ctx, Long userId, Long appId, Long roomId) {
        //按照userId保存好相关的channel对象信息
        ChannelHandlerContextCache.put(userId, ctx);
        ImContextUtils.setUserId(ctx, userId);
        ImContextUtils.setAppId(ctx, appId);
        if (roomId != null) {
            ImContextUtils.setRoomId(ctx, roomId);
        }
        //将im消息回写给客户端
        ImMsgBodyInTcpWsDto respBody = new ImMsgBodyInTcpWsDto();
        respBody.setAppId(appId);
        respBody.setToUserId(userId);
        respBody.setData("true");
        ImTcpWsDto respMsg = ImTcpWsDto.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));
        stringRedisTemplate.opsForValue().set(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId,
                ChannelHandlerContextCache.getServerIpAddress() + "%" + userId,
                ImConstants.DEFAULT_HEART_BEAT_GAP * 2, TimeUnit.SECONDS);

        stringRedisTemplate.opsForValue().set(ImCoreServerConstants.IM_WS_BIND_IP_KEY + appId + ":" + userId,
                ChannelHandlerContextCache.getWsIpAddress(),
                ImConstants.DEFAULT_HEART_BEAT_GAP * 2, TimeUnit.SECONDS);
        log.info("[LoginMsgHandler] login success,userId is {},appId is {}", userId, appId);
        ctx.writeAndFlush(respMsg);
        if (roomId != null){
            this.sendLoginMQ(userId, appId, roomId);
        }

    }
}
