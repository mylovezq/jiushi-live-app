package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import top.mylove7.live.common.interfaces.dto.ImMsgBody;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimplyHandler;
import org.qiyu.live.im.core.server.service.IMsgAckCheckService;
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
public class AckImMsgHandler implements SimplyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AckImMsgHandler.class);

    @Resource
    private IMsgAckCheckService msgAckCheckService;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        Long userId = ImContextUtils.getUserId(ctx);
        Long appid = ImContextUtils.getAppId(ctx);
        if (userId == null && appid == null) {
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        msgAckCheckService.doMsgAck(JSON.parseObject(imMsg.getBody(), ImMsgBody.class));
    }
}
