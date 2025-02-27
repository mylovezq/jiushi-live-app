package top.mylove7.live.im.core.server.common;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @Author jiushi
 * @Date created in 9:29 下午 2022/12/22
 */
public class WebsocketEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof ImTcpWsDto)) {
            super.write(ctx, msg, promise);
            return;
        }
        ImTcpWsDto imTcpWsDto = (ImTcpWsDto) msg;
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imTcpWsDto)));
    }
}
