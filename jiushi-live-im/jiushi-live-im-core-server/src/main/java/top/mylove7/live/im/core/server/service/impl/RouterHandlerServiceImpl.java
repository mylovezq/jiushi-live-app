package top.mylove7.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import top.mylove7.jiushi.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.constants.ImMsgCodeEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.im.core.server.common.ChannelHandlerContextCache;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.im.core.server.service.IMsgAckCheckService;
import top.mylove7.live.im.core.server.service.IRouterHandlerService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
@Slf4j
public class RouterHandlerServiceImpl implements IRouterHandlerService {

    @Resource
    private IMsgAckCheckService msgAckCheckService;

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    @Override
    public void onReceive(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {

        //需要进行消息通知的userid
        if(this.sendMsgToClient(imMsgBodyInTcpWsDto)) {
            //当im服务器推送了消息给到客户端，然后我们需要记录下ack
            msgAckCheckService.recordMsgAck(imMsgBodyInTcpWsDto, 1);
            //延迟检查是否确认消息收到了
            msgAckCheckService.sendDelayMsg(imMsgBodyInTcpWsDto);
        }
    }

    @Override
    @SneakyThrows
    public boolean sendMsgToClient(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        boolean hadSendMsgTag = msgAckCheckService.hadMsgAck(imMsgBodyInTcpWsDto);
        if (hadSendMsgTag) {
            log.info("该用户{}的该条mq消息的im信息已经发送,并且已经确认{}", imMsgBodyInTcpWsDto.getUserId(), imMsgBodyInTcpWsDto.getMsgId());
            return false;
        }
        Long userId = imMsgBodyInTcpWsDto.getUserId();
        ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);
        if (ctx != null) {
            ImTcpWsDto respMsg = ImTcpWsDto.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBodyInTcpWsDto));
            ctx.writeAndFlush(respMsg);
            return true;
        }
        return false;
    }
}
