package top.mylove7.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import top.mylove7.live.common.interfaces.constants.ImConstants;


import java.util.List;

/**
 * 消息解码器
 *
 * @Author jiushi
 *
 * @Description
 */
public class TcpImMsgDecoder extends ByteToMessageDecoder {

    private final int BASE_LEN = 2 + 4 + 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        //bytebuf内容的基本校验，长度校验，magic值校验
        if (byteBuf.readableBytes() >= BASE_LEN) {
            if (byteBuf.readShort() != ImConstants.DEFAULT_MAGIC) {
                ctx.close();
                return;
            }
            int code = byteBuf.readInt();
            int len = byteBuf.readInt();
            //确保bytebuf剩余的消息长度足够
            if (byteBuf.readableBytes() < len) {
                ctx.close();
                return;
            }
            byte[] body = new byte[len];
            byteBuf.readBytes(body);
            //将bytebuf转换为immsg对象
            ImTcpWsDto imTcpWsDto = new ImTcpWsDto();
            imTcpWsDto.setCode(code);
            imTcpWsDto.setLen(len);
            imTcpWsDto.setMagic(ImConstants.DEFAULT_MAGIC);
            imTcpWsDto.setBody(body);
            out.add(imTcpWsDto);
        }
    }
}
