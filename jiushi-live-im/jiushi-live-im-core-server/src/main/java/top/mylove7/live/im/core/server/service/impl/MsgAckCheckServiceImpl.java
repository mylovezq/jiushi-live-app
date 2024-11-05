package top.mylove7.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.util.Assert;
import top.mylove7.jiushi.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import top.mylove7.live.im.core.server.dao.ImMsgMongoDo;
import top.mylove7.live.im.core.server.interfaces.dto.ImAckDto;
import top.mylove7.live.im.core.server.service.IMsgAckCheckService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void doMsgAck(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto, Long roomId) {
        String hadSendMsgKey = cacheKeyBuilder.buildHadSendMsgKey(imMsgBodyInTcpWsDto.getAppId(), roomId, imMsgBodyInTcpWsDto.getToUserId());
        ImAckDto imAckDto = (ImAckDto) redisTemplate.opsForHash().get(hadSendMsgKey, imMsgBodyInTcpWsDto.getMsgId());
        if (imAckDto == null){
            log.error("没有该消息{}",hadSendMsgKey+":"+imMsgBodyInTcpWsDto.getMsgId());
            return;
        }
        imAckDto.setHadAck(true);
        redisTemplate.opsForHash().put(hadSendMsgKey, imMsgBodyInTcpWsDto.getMsgId(), imAckDto);

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
