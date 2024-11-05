package top.mylove7.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.im.core.server.common.ImContextUtils;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.im.core.server.handler.SimplyHandler;
import top.mylove7.live.im.core.server.service.IMsgAckCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 登出消息的处理逻辑统一收拢到这个类中
 *
 * @Author jiushi
 *
 * @Description
 */
@Component
@Slf4j
public class AckImMsgHandler implements SimplyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AckImMsgHandler.class);

    @Resource
    private IMsgAckCheckService msgAckCheckService;

    @Override
    public void handler(ChannelHandlerContext ctx, ImTcpWsDto imTcpWsDto) {
        Long userId = ImContextUtils.getUserId(ctx);
        Long appid = ImContextUtils.getAppId(ctx);
        Long roomId = ImContextUtils.getRoomId(ctx);
        if (userId == null && appid == null) {
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = JSON.parseObject(imTcpWsDto.getBody(), ImMsgBodyInTcpWsDto.class);
        imMsgBodyInTcpWsDto.setToUserId(userId);

        msgAckCheckService.doMsgAck(imMsgBodyInTcpWsDto,roomId);
    }
}
