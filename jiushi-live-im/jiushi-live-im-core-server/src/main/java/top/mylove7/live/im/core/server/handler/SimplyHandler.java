package top.mylove7.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface SimplyHandler {

    /**
     * 消息处理函数
     *
     * @param ctx
     * @param imTcpWsDto
     */
    void handler(ChannelHandlerContext ctx, ImTcpWsDto imTcpWsDto);
}
