package top.mylove7.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 处理消息的编码过程
 *
 * @Author jiushi
 *
 * @Description
 */
public class TcpImMsgEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        ImTcpWsDto imTcpWsDto = (ImTcpWsDto) msg;
        out.writeShort(imTcpWsDto.getMagic());
        out.writeInt(imTcpWsDto.getCode());
        out.writeInt(imTcpWsDto.getLen());
        out.writeBytes(imTcpWsDto.getBody());
    }
}
