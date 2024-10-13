package top.mylove7.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import top.mylove7.live.common.interfaces.constants.ImMsgCodeEnum;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.im.core.server.handler.ImHandlerFactory;
import top.mylove7.live.im.core.server.handler.SimplyHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiushi
 *
 * @Description
 */
@Component
public class ImHandlerFactoryImpl implements ImHandlerFactory, InitializingBean {

    private static final Map<Integer, SimplyHandler> simplyHandlerMap = new HashMap<>();
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void doMsgHandler(ChannelHandlerContext channelHandlerContext, ImTcpWsDto imTcpWsDto) {
        SimplyHandler simplyHandler = simplyHandlerMap.get(imTcpWsDto.getCode());
        if (simplyHandler == null) {
            throw new IllegalArgumentException("msg code is error,code is :" + imTcpWsDto.getCode());
        }
        simplyHandler.handler(channelHandlerContext, imTcpWsDto);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //登录消息包，登录token认证，channel 和userId关联
        //等出消息包，正常断开im连接的时候发送的
        //业务消息包，最常用的消息类型，例如我们的im发送数据，或者接收数据的时候会用到
        //心跳消息包，定时会给im发送，汇报功能
        simplyHandlerMap.put(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(),applicationContext.getBean(LoginMsgHandler.class));
        simplyHandlerMap.put(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), applicationContext.getBean(LogoutMsgHandler.class));
        simplyHandlerMap.put(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), applicationContext.getBean(BizImMsgHandler.class));
        simplyHandlerMap.put(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), applicationContext.getBean(HeartBeatImMsgHandler.class));
        simplyHandlerMap.put(ImMsgCodeEnum.IM_ACK_MSG.getCode(), applicationContext.getBean(AckImMsgHandler.class));
    }
}
