package top.mylove7.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import top.mylove7.jiushi.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import top.mylove7.live.im.core.server.dao.ImMsgMongoDo;
import top.mylove7.live.im.core.server.service.IMsgAckCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.mylove7.live.im.core.server.service.ImMsgMongoService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author jiushi
 * @Description
 */
@Service
@Slf4j
public class MsgAckCheckServiceImpl implements IMsgAckCheckService {


    @Resource
    private MQProducer mqProducer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImMsgMongoService imMsgMongoService;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void doMsgAck(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        RLock imMsgSendLock = this.getImMsgSendLock(imMsgBodyInTcpWsDto);
        try {
            if (!imMsgSendLock.tryLock(10, 10, TimeUnit.SECONDS)) {
                log.error("等待锁时 获取锁失败");
                throw new BizErrorException("等待锁时 获取锁失败");
            }
            boolean imMsgMongoUpdate = imMsgMongoService.lambdaUpdate()
                    .eq(ImMsgMongoDo::getId, imMsgBodyInTcpWsDto.getMsgId())
                    .set(ImMsgMongoDo::getHadAck, true)
                    .set(ImMsgMongoDo::getUpdateTime, LocalDateTime.now())
                    .update();
            //一般不会失败，数据是存好了才发消息给客户端
            if (!imMsgMongoUpdate){
                log.error("更新im确认消息异常{}",imMsgBodyInTcpWsDto);
            }

        } catch (Exception e) {
            log.error("获取锁，更新im确认消息异常", e);
            throw new BizErrorException("im确认消息异常");
        }

    }


    @Override
    public RLock getImMsgSendLock(ImMsgBodyInTcpWsDto inTcpDto) {
        Long roomId;
        ImMsgMongoDo imMsgMongoDo = imMsgMongoService.lambdaQuery().eq(ImMsgMongoDo::getId, inTcpDto.getMsgId()).one();
        if (imMsgMongoDo != null){
            inTcpDto.setAppId(imMsgMongoDo.getAppId());
            inTcpDto.setFromUserId(imMsgMongoDo.getFromUserId());
            inTcpDto.setToUserId(imMsgMongoDo.getToUserId());
            roomId = imMsgMongoDo.getRoomId();
        }else {
            JSONObject jsonObject = JSON.parseObject(inTcpDto.getData());
            roomId = jsonObject.getLong("roomId");
        }
        return redissonClient.getLock((inTcpDto.getAppId()
                + inTcpDto.getFromUserId()
                + inTcpDto.getToUserId()
                + roomId
                + inTcpDto.getMsgId()));
    }


    @Override
    public void sendDelayMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        String json = JSON.toJSONString(imMsgBodyInTcpWsDto);
        Message message = new Message();
        message.setBody(json.getBytes());
        message.setTopic(ImCoreServerProviderTopicNames.JIUSHI_LIVE_IM_ACK_MSG_TOPIC);
        //等级1 -> 1s，等级2 -> 5s
        message.setDelayTimeLevel(2);
        try {
            SendResult sendResult = mqProducer.send(message);
            log.info("[MsgAckCheckServiceImpl] msg is {},sendResult is {}", json, sendResult);
        } catch (Exception e) {
            log.error("[MsgAckCheckServiceImpl] error is ", e);
        }
    }
}
