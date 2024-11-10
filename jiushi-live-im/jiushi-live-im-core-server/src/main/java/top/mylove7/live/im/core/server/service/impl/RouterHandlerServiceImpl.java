package top.mylove7.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import top.mylove7.jiushi.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.constants.ImMsgCodeEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.im.core.server.common.ChannelHandlerContextCache;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.im.core.server.interfaces.dto.ImAckDto;
import top.mylove7.live.im.core.server.service.IMsgAckCheckService;
import top.mylove7.live.im.core.server.service.IRouterHandlerService;
import org.springframework.stereotype.Service;
import top.mylove7.live.im.core.server.service.ImMsgMongoService;

import java.util.concurrent.TimeUnit;

/**
 * @Author jiushi
 * @Description
 */
@Service
@Slf4j
public class RouterHandlerServiceImpl implements IRouterHandlerService {

    @Resource
    private IMsgAckCheckService msgAckCheckService;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private ImMsgMongoService imMsgMongoService;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void onReceive(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {

        //需要进行消息通知的userid
        if (this.sendMsgToClient(imMsgBodyInTcpWsDto)) {
            //延迟检查是否确认消息收到了
            msgAckCheckService.sendDelayMsg(imMsgBodyInTcpWsDto);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendMsgToClient(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {

        try {
            boolean isGoOnSend = this.checkOrAddImMsgRecord(imMsgBodyInTcpWsDto);

            if (!isGoOnSend) {
                return false;
            }

            Long userId = imMsgBodyInTcpWsDto.getToUserId();
            ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);

            if (ctx != null) {

                ImTcpWsDto respMsg = ImTcpWsDto.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBodyInTcpWsDto));

                ctx.writeAndFlush(respMsg);

                return true;
            }

            log.error("客户端已下线");
            throw new BizErrorException("客户端已下线");
        } catch (BizErrorException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * 由于是聊天室的聊天消息，直播间关闭，聊天消息不再重要，缓存到redis即可
     * <p>
     * 也不必考虑记录重复次数，因为本来就延迟了去重复，mq也延迟了消费，再由前端去重兜底显示即可
     *
     * @param imMsg
     * @return
     */
    private boolean checkOrAddImMsgRecord(ImMsgBodyInTcpWsDto imMsg) {
        Long roomId = JSON.parseObject(imMsg.getData()).getLong("roomId");
        String hadSendMsgKey = cacheKeyBuilder.buildHadSendMsgKey(imMsg.getAppId(), roomId, imMsg.getToUserId());
        ImAckDto imAckDto = (ImAckDto) redisTemplate.opsForHash().get(hadSendMsgKey, imMsg.getMsgId());

        if (imAckDto != null && imAckDto.getRetryTime() > 3 ) {
            log.info("该消息达到最大发送次数");
            return false;
        }
        if (imAckDto != null && imAckDto.getHadAck()) {
            log.info("该消息已发送并确认到达");
            return false;
        }
        if (imAckDto == null) {
            imAckDto = new ImAckDto();
            imAckDto.setRetryTime(0);
            imAckDto.setHadAck(false);
            redisTemplate.opsForHash().put(hadSendMsgKey, imMsg.getMsgId(), imAckDto);
            return true;
        }
        imAckDto.setRetryTime(imAckDto.getRetryTime() + 1);
        redisTemplate.opsForHash().put(hadSendMsgKey, imMsg.getMsgId(), imAckDto);
        redisTemplate.expire(hadSendMsgKey, 12, TimeUnit.HOURS);
        return true;

    }


}
