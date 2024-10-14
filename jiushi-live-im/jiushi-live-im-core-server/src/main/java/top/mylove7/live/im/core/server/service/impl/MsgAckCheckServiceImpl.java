package top.mylove7.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import top.mylove7.jiushi.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import top.mylove7.live.im.core.server.service.IMsgAckCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
@Slf4j
public class MsgAckCheckServiceImpl implements IMsgAckCheckService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MsgAckCheckServiceImpl.class);

    @Resource
    private MQProducer mqProducer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public void doMsgAck(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        String key = cacheKeyBuilder.buildImAckMapKey(imMsgBodyInTcpWsDto.getUserId(), imMsgBodyInTcpWsDto.getAppId());
        redisTemplate.opsForHash().put(key, imMsgBodyInTcpWsDto.getMsgId(),-1);
        redisTemplate.expire(key,60, TimeUnit.MINUTES);
        log.info("WebsocketCoreHandler确认消息收到成功{}",imMsgBodyInTcpWsDto.getMsgId());

    }

    @Override
    public boolean hadMsgAck(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        Integer msgAckTimes = this.getMsgAckTimes(imMsgBodyInTcpWsDto.getMsgId(), imMsgBodyInTcpWsDto.getUserId(), imMsgBodyInTcpWsDto.getAppId());
        log.info("检查im消息是否确认收到msgAckTimes is {}", msgAckTimes);
        if (Objects.equals(msgAckTimes,-1)){
            log.info("im消息{}已经确定收到", imMsgBodyInTcpWsDto.getMsgId());
            return true;
        }
        return false;

    }
    private static final DefaultRedisScript<Boolean> SET_TIMES_NOT_EXIST = new DefaultRedisScript<>();
    static {
        SET_TIMES_NOT_EXIST.setScriptText(
                "local key = KEYS[1]\n" +
                        "local msgId = ARGV[1]\n" +
                        "local times = ARGV[2]\n" +
                        "if redis.call('hexists', key, msgId) == 0 then\n" +
                        "    redis.call('hset', key, msgId, times)\n" +
                        "    return true\n" +
                        "else\n" +
                        "    return false\n" +
                        "end"
        );
        SET_TIMES_NOT_EXIST.setResultType(Boolean.class);
    }
    private static final DefaultRedisScript<Boolean> SET_TIMES_GE_0 = new DefaultRedisScript<>();
    static {
        SET_TIMES_GE_0.setScriptText(
                "local key = KEYS[1]\n" +
                        "local msgId = ARGV[1]\n" +
                        "local times = tonumber(ARGV[2])\n" +
                        "local currentTimes = tonumber(redis.call('hget', key, msgId))\n" +
                        "if currentTimes == nil or currentTimes <= 0 then\n" +
                        "    return false\n" +
                        "else\n" +
                        "    redis.call('hset', key, msgId, times)\n" +
                        "    return true\n" +
                        "end"
        );
        SET_TIMES_GE_0.setResultType(Boolean.class);
    }

    @Override
    public void recordMsgAck(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto, int times) {
        if (times < 0){
            return;
        }
        //说明是第一次，则lua脚本需要保证key不存在
        String key = cacheKeyBuilder.buildImAckMapKey(imMsgBodyInTcpWsDto.getUserId(), imMsgBodyInTcpWsDto.getAppId());
        if (times == 1){
            Boolean result = redisTemplate.execute(
                    SET_TIMES_NOT_EXIST,
                    Collections.singletonList(key),
                    imMsgBodyInTcpWsDto.getMsgId(),
                    String.valueOf(times));
            log.info("第一次存入确认消息结果{}",result);
            if (result){
                redisTemplate.expire(key,60, TimeUnit.MINUTES);
            }else {
                Object laserTimes = redisTemplate.opsForHash().get(key, imMsgBodyInTcpWsDto.getMsgId());
                log.info("第一次存入确认消息失败{}，消息可能已经确认收到{}",key + imMsgBodyInTcpWsDto.getMsgId(),laserTimes);
            }
            return;
        }
        Boolean result = redisTemplate.execute(
                SET_TIMES_GE_0,
                Collections.singletonList(key),
                imMsgBodyInTcpWsDto.getMsgId(),
                String.valueOf(times));
        log.info("确认消息是否被接收到确认消息结果{}",result);
        if (result){
            redisTemplate.expire(key,60, TimeUnit.MINUTES);
        }else {
            Object laserTimes = redisTemplate.opsForHash().get(key, imMsgBodyInTcpWsDto.getMsgId());
            log.info("更新失败，消息已被确认收到{}",key + imMsgBodyInTcpWsDto.getMsgId(),laserTimes,laserTimes);

        }
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
            LOGGER.info("[MsgAckCheckServiceImpl] msg is {},sendResult is {}", json, sendResult);
        } catch (Exception e) {
            LOGGER.error("[MsgAckCheckServiceImpl] error is ", e);
        }
    }

    @Override
    public Integer getMsgAckTimes(String msgId, Long userId, Long appId) {
        Object value = redisTemplate.opsForHash().get(cacheKeyBuilder.buildImAckMapKey(userId, appId), msgId);
        log.info("获取的消息存入的key是{}", cacheKeyBuilder.buildImAckMapKey(userId, appId)+":"+msgId);

        if (value == null) {
            return null;
        }
        return (int) value;
    }
}
