package top.mylove7.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.jiushi.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.constants.ImConstants;
import top.mylove7.live.common.interfaces.constants.ImMsgCodeEnum;
import top.mylove7.live.im.core.server.common.ImContextUtils;
import top.mylove7.live.im.core.server.handler.SimplyHandler;
import top.mylove7.live.common.interfaces.constants.ImCoreServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 心跳消息处理器
 *
 * @Author jiushi
 *
 * @Description
 */
@Component
@Slf4j
public class HeartBeatImMsgHandler implements SimplyHandler {


    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public void handler(ChannelHandlerContext ctx, ImTcpWsDto imTcpWsDto) {
        //心跳包基本校验
        Long userId = ImContextUtils.getUserId(ctx);
        Long appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            log.error("attr error,imTcpWsDto is {}", imTcpWsDto);
            //有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        //心跳包record记录，redis存储心跳 记录
        String redisKey = cacheKeyBuilder.buildImLoginTokenKey(userId, appId);
        this.recordOnlineTime(userId, redisKey);
        this.removeExpireRecord(redisKey);
        redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
        //延长用户之前保存的ip绑定记录时间
        stringRedisTemplate.expire(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId, ImConstants.DEFAULT_HEART_BEAT_GAP * 2, TimeUnit.SECONDS);
        ImMsgBodyInTcpWsDto msgBody = new ImMsgBodyInTcpWsDto();
        msgBody.setToUserId(userId);
        msgBody.setAppId(appId);
        msgBody.setData("心跳包处理成功");
        ImTcpWsDto respMsg = ImTcpWsDto.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(msgBody));
        //log.info("[HeartBeatImMsgHandler] imTcpWsDto is {}", imTcpWsDto.toJson());
        ctx.writeAndFlush(respMsg);
    }

    /**
     * 清理掉过期不在线的用户留下的心跳记录(在两次心跳包的发送间隔中，如果没有重新更新score值，就会导致被删除)
     *
     * @param redisKey
     */
    private void removeExpireRecord(String redisKey) {
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, System.currentTimeMillis() - ImConstants.DEFAULT_HEART_BEAT_GAP * 1000 * 2);
    }

    /**
     * 记录用户最近一次心跳时间到zSet上
     *
     * @param userId
     * @param redisKey
     */
    private void recordOnlineTime(Long userId, String redisKey) {
        redisTemplate.opsForZSet().add(redisKey, userId, System.currentTimeMillis());
    }


}
