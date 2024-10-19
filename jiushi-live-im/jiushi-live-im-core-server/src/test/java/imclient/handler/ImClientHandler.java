package imclient.handler;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.dubbo.config.annotation.DubboReference;

import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.account.interfaces.im.ImTokenRpc;
import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.constants.ImMsgCodeEnum;
import top.mylove7.live.im.core.server.common.TcpImMsgDecoder;
import top.mylove7.live.im.core.server.common.TcpImMsgEncoder;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class ImClientHandler implements InitializingBean {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                EventLoopGroup clientGroup = new NioEventLoopGroup();
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(clientGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("初始化连接建立");
                        ch.pipeline().addLast(new TcpImMsgDecoder());
                        ch.pipeline().addLast(new TcpImMsgEncoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });

                ChannelFuture channelFuture = null;
                try {
                    channelFuture = bootstrap.connect("localhost", 18085).sync();
                    Channel channel = channelFuture.channel();
                    Long userId = 991219L;
//                    Scanner scanner = new Scanner(System.in);
//                    System.out.println("请输入userId");
//                    Long userId = scanner.nextLong();
//                    System.out.println("请输入objectId");
//                    Long objectId = scanner.nextLong();
                    String token = imTokenRpc.createImLoginToken(userId, AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
                    //发送登录消息包
                    ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = new ImMsgBodyInTcpWsDto();
                    imMsgBodyInTcpWsDto.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
                    imMsgBodyInTcpWsDto.setToken(token);
                    imMsgBodyInTcpWsDto.setToUserId(userId);
                    ImTcpWsDto loginMsg = ImTcpWsDto.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(imMsgBodyInTcpWsDto));
                    channel.writeAndFlush(loginMsg);

                    while (true) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", userId);
                        jsonObject.put("objectId", 1L);
                        jsonObject.put("content", "hello world" + UUID.randomUUID());
                        imMsgBodyInTcpWsDto.setData(String.valueOf(jsonObject));
                        ImTcpWsDto loginMsg1 = ImTcpWsDto.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBodyInTcpWsDto));
                        channel.writeAndFlush(loginMsg1);
                    }

                    //心跳包机制
                    //sendHeartBeat(userId, channel);
//                    while (true) {
//                        System.out.println("请输入聊天内容");
//                        String content = scanner.nextLine();
//                        if (StringUtils.isEmpty(content)) {
//                            continue;
//                        }
//                        ImMsgBodyInTcpWsDto bizBody = new ImMsgBodyInTcpWsDto();
//                        bizBody.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
//                        bizBody.setUserId(userId);
//                        bizBody.setBizCode(5555);
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("userId", userId);
//                        jsonObject.put("objectId", objectId);
//                        jsonObject.put("content", content);
//                        bizBody.setData(JSON.toJSONString(jsonObject));
//                        ImTcpWsDto heartBeatMsg = ImTcpWsDto.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(bizBody));
//                        channel.writeAndFlush(heartBeatMsg);
//                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        clientThread.start();
    }


    private void sendHeartBeat(Long userId, Channel channel) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = new ImMsgBodyInTcpWsDto();
                imMsgBodyInTcpWsDto.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
                imMsgBodyInTcpWsDto.setToUserId(userId);
                ImTcpWsDto loginMsg = ImTcpWsDto.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(imMsgBodyInTcpWsDto));
                channel.writeAndFlush(loginMsg);
            }
        }).start();
    }
}
