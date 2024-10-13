package top.mylove7.live.im.core.server.handler.tcp;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import top.mylove7.live.im.core.server.common.ImContextUtils;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.im.core.server.handler.ImHandlerFactory;
import top.mylove7.live.im.core.server.handler.impl.LogoutMsgHandler;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * im消息统一handler入口
 *
 * @Author jiushi
 *
 * @Description
 */
@Component
@ChannelHandler.Sharable
public class TcpImServerCoreHandler extends SimpleChannelInboundHandler {

    @Resource
    private ImHandlerFactory imHandlerFactory;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LogoutMsgHandler logoutMsgHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)  {
        if (!(msg instanceof ImTcpWsDto)) {
            throw new IllegalArgumentException("error msg,msg is :" + msg);
        }
        ImTcpWsDto imTcpWsDto = (ImTcpWsDto) msg;
        imHandlerFactory.doMsgHandler(ctx, imTcpWsDto);
    }

    /**
     * 正常或者意外断线，都会触发到这里
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx)  {
        Long userId = ImContextUtils.getUserId(ctx);
        Long appId = ImContextUtils.getAppId(ctx);
        if (userId != null && appId != null) {
            logoutMsgHandler.logoutHandler(ctx,userId,appId);
        }
    }
}
