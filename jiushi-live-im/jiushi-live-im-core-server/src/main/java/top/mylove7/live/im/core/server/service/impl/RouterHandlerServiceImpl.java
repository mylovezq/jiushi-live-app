package top.mylove7.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.anwen.mongo.incrementer.id.IdWorker;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import top.mylove7.jiushi.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.constants.ImMsgCodeEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.im.core.server.common.ChannelHandlerContextCache;
import top.mylove7.live.im.core.server.common.ImTcpWsDto;
import top.mylove7.live.im.core.server.dao.ImMsgMongoDo;
import top.mylove7.live.im.core.server.service.IMsgAckCheckService;
import top.mylove7.live.im.core.server.service.IRouterHandlerService;
import org.springframework.stereotype.Service;
import top.mylove7.live.im.core.server.service.ImMsgMongoService;

import java.time.LocalDateTime;
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
    @Transactional(rollbackFor =Exception.class)
    public boolean sendMsgToClient(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {

        redisTemplate.opsForZSet().add("", imMsgBodyInTcpWsDto, System.currentTimeMillis());

        RLock imMsgSendLock = null;
        try {
            imMsgSendLock = msgAckCheckService.getImMsgSendLock(imMsgBodyInTcpWsDto);
            boolean tryLock = imMsgSendLock.tryLock(10, 10, TimeUnit.SECONDS);
            if (!tryLock) {
                log.error("等待锁时 获取锁失败");
                throw new BizErrorException("等待锁时 获取锁失败");
            }
            imMsgBodyInTcpWsDto.setMsgId(IdWorker.getId()+"");
            boolean isGoOnSend = this.checkOrAddImMsgRecord(imMsgBodyInTcpWsDto);

            if (!isGoOnSend){
                log.info("该消息已发送并确认，或者达到最大发送次数");
                return false;
            }

            Long userId = imMsgBodyInTcpWsDto.getToUserId();
            ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);

            if (ctx != null) {

                ImTcpWsDto respMsg = ImTcpWsDto.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBodyInTcpWsDto));

                ctx.writeAndFlush(respMsg);
                //记录消息到mongodb
                return true;
            }

            log.error("客户端已下线");
            throw new BizErrorException("客户端已下线");



        } catch (Exception e) {
            log.error("发送消息给客户端异常", e);
            throw new BizErrorException("发送消息给客户端异常");
        } finally {
            if (null != imMsgSendLock && imMsgSendLock.isHeldByCurrentThread()) {
                imMsgSendLock.unlock();
            }
        }


    }

    private boolean checkOrAddImMsgRecord(ImMsgBodyInTcpWsDto imMsg) {
        Long roomId =  JSON.parseObject(imMsg.getData()).getLong("roomId");

        ImMsgMongoDo imMsgMongoDo = imMsgMongoService.lambdaQuery()
                .eq(ImMsgMongoDo::getAppId, imMsg.getAppId())
                .eq(ImMsgMongoDo::getFromUserId, imMsg.getFromUserId())
                .eq(ImMsgMongoDo::getToUserId, imMsg.getToUserId())
                .eq(ImMsgMongoDo::getRoomId, roomId )
                .eq(ImMsgMongoDo::getMessageId, imMsg.getMsgId())
                .one();
        if (imMsgMongoDo == null){
            ImMsgMongoDo imMsgMongoDoNew = new ImMsgMongoDo(imMsg.getAppId(), imMsg.getFromUserId(), imMsg.getToUserId(),roomId, imMsg.getMsgId(), imMsg.toByte());
            imMsgMongoDoNew.setCreateTime(LocalDateTime.now());
            imMsgMongoDoNew.setHadAck(false);
            imMsgMongoDoNew.setRetryTime(0);
            imMsgMongoDoNew.setId(Long.valueOf(imMsg.getMsgId()));
            imMsgMongoService.save(imMsgMongoDoNew);
            return true;
        }
        Boolean hadAck = imMsgMongoDo.getHadAck();
        Integer retryTime = imMsgMongoDo.getRetryTime();
        //未收到确认消息 且 重试次数小于3次
        boolean canRetrySend = !hadAck && retryTime < 3;
        if (canRetrySend){
            //更新次数
            boolean update = imMsgMongoService.lambdaUpdate()
                    .set(ImMsgMongoDo::getRetryTime, retryTime + 1)
                    .eq(ImMsgMongoDo::getId, imMsgMongoDo.getId()).update();
            if (!update){
                log.error("mongo数据更新消息记录失败 {}",imMsgMongoDo);
            }
        }

        return canRetrySend;

    }



}
