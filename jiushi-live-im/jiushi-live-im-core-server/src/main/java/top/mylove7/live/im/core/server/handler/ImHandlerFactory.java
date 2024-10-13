package top.mylove7.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ImHandlerFactory {

    /**
     * 按照immsg的code去筛选
     *
     * @param channelHandlerContext
     * @param imTcpWsDto
     */
    void doMsgHandler(ChannelHandlerContext channelHandlerContext, ImTcpWsDto imTcpWsDto);
}
