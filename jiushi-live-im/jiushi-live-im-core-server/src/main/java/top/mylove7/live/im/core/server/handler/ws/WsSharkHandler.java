package top.mylove7.live.im.core.server.handler.ws;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import top.mylove7.live.common.interfaces.dto.ImUserInfoTokenDto;
import top.mylove7.live.common.interfaces.enums.GatewayHeaderEnum;
import top.mylove7.live.im.core.server.common.ChannelHandlerContextCache;
import top.mylove7.live.im.core.server.handler.impl.LoginMsgHandler;
import org.springframework.stereotype.Component;
import top.mylove7.live.user.interfaces.auth.interfaces.im.ImTokenRpc;

/**
 * ws的握手连接处理器
 *
 * @Author jiushi
 * @Date created in 9:30 下午 2022/12/22
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class WsSharkHandler extends ChannelInboundHandlerAdapter {


    //指定监听的端口
    @DubboReference
    private ImTokenRpc imTokenRpc;
    @Resource
    private LoginMsgHandler loginMsgHandler;

    private WebSocketServerHandshaker webSocketServerHandshaker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //握手接入ws
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, ((FullHttpRequest) msg));
            return;
        }

        //正常关闭链路
        if (msg instanceof CloseWebSocketFrame) {
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) ((WebSocketFrame) msg).retain());
            return;
        }
        //将消息传递给下一个链路处理器去处理
        ctx.fireChannelRead(msg);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        // ws://127.0.0.1:8809/token={token}&userId={userId}&{roomId}=roomId
        // ws://127.0.0.1:8809/{token}/{userId}/{code}/{param}
        // 基于code去做不同策略的参数解析
        String webSocketUrl = "ws://" + ChannelHandlerContextCache.getWsIpAddress();
        // 构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketUrl, null, false);

        //建立ws的握手连接
        webSocketServerHandshaker = wsFactory.newHandshaker(msg);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }
        ChannelFuture channelFuture = webSocketServerHandshaker.handshake(ctx.channel(), msg);
        //首次握手建立ws连接后，返回一定的内容给到客户端
        if (channelFuture.isSuccess()) {
            String imUserInfoStr = msg.headers().get(GatewayHeaderEnum.IM_USER_LOGIN_INFO.getName());

            ImUserInfoTokenDto imUserInfo = ImUserInfoTokenDto.fromJson(imUserInfoStr);

            Long userId = imUserInfo.getUserId();
            Long appId = imUserInfo.getAppId();
            Long roomId = imUserInfo.getRoomId();
            loginMsgHandler.loginSuccessHandler(ctx, userId, appId, roomId);
            log.info("[WebsocketSharkHandler] channel is connect!");
        }
    }
}
