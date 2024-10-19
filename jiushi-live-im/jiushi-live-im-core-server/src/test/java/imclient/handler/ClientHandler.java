package imclient.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.common.interfaces.constants.ImMsgCodeEnum;

/**
 * @Author jiushi
 *
 * @Description
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ImTcpWsDto imTcpWsDto = (ImTcpWsDto) msg;
        if (imTcpWsDto.getCode() == ImMsgCodeEnum.IM_BIZ_MSG.getCode()) {
            ImMsgBodyInTcpWsDto respBody = JSON.parseObject(new String(imTcpWsDto.getBody()), ImMsgBodyInTcpWsDto.class);
            ImMsgBodyInTcpWsDto ackBody = new ImMsgBodyInTcpWsDto();
            ackBody.setMsgId(respBody.getMsgId());
            ackBody.setAppId(respBody.getAppId());
            ackBody.setToUserId(respBody.getToUserId());
            ImTcpWsDto ackMsg = ImTcpWsDto.build(ImMsgCodeEnum.IM_ACK_MSG.getCode(), JSON.toJSONString(ackBody));
            ctx.writeAndFlush(ackMsg);
        }

        System.out.println("【原始数据】result is " + imTcpWsDto);
        System.out.println("【服务端响应数据】result is " + new String(imTcpWsDto.getBody()));

    }
}
