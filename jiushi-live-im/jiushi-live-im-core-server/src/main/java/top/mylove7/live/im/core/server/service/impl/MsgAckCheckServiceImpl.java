package top.mylove7.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import top.mylove7.jiushi.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import top.mylove7.live.im.core.server.service.IMsgAckCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
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
        redisTemplate.opsForHash().delete(key, imMsgBodyInTcpWsDto.getMsgId());
        redisTemplate.expire(key,30, TimeUnit.MINUTES);
    }

    @Override
    public void recordMsgAck(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto, int times) {
        String key = cacheKeyBuilder.buildImAckMapKey(imMsgBodyInTcpWsDto.getUserId(), imMsgBodyInTcpWsDto.getAppId());
        redisTemplate.opsForHash().put(key, imMsgBodyInTcpWsDto.getMsgId(), times);
        redisTemplate.expire(key,30, TimeUnit.MINUTES);
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
    public int getMsgAckTimes(String msgId, Long userId, Long appId) {
        Object value = redisTemplate.opsForHash().get(cacheKeyBuilder.buildImAckMapKey(userId, appId), msgId);
        if (value == null) {
            return -1;
        }
        return (int) value;
    }
}
