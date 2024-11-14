package top.mylove7.live.im.core.server.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import top.mylove7.jiushi.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import top.mylove7.live.im.core.server.interfaces.dto.ImAckDto;
import top.mylove7.live.im.core.server.service.IMsgAckCheckService;

import static org.apache.rocketmq.client.producer.SendStatus.SEND_OK;

/**
 * @Author jiushi
 * @Description
 */
@Service
@Slf4j
public class MsgAckCheckServiceImpl implements IMsgAckCheckService {


    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void doMsgAck(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto, Long roomId) {
        String hadSendMsgKey = cacheKeyBuilder.buildHadSendMsgKey(imMsgBodyInTcpWsDto.getAppId(), roomId, imMsgBodyInTcpWsDto.getToUserId());
        if (StrUtil.isBlank(imMsgBodyInTcpWsDto.getMsgId())) {
            log.error("没有消息id，非法请求");
            return;
        }
        ImAckDto imAckDto = (ImAckDto) redisTemplate.opsForHash().get(hadSendMsgKey, imMsgBodyInTcpWsDto.getMsgId());
        if (imAckDto == null) {
            log.error("没有该消息{}", hadSendMsgKey + ":" + imMsgBodyInTcpWsDto.getMsgId());
            return;
        }
        imAckDto.setHadAck(true);
        redisTemplate.opsForHash().put(hadSendMsgKey, imMsgBodyInTcpWsDto.getMsgId(), imAckDto);

    }


    @Override
    public void sendDelayMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        SendResult sendResult
                = rocketMQTemplate.syncSend(ImCoreServerProviderTopicNames.JIUSHI_LIVE_IM_ACK_MSG_TOPIC, MessageBuilder.withPayload(imMsgBodyInTcpWsDto).build() , 5000,2);
        log.info("发送ack消息延迟确认消息result:{}", sendResult);
        if (sendResult.getSendStatus() != SEND_OK) {
            log.error("发送ack消息延迟确认消息失败");
            //可以记录到mongodb
            throw new BizErrorException("发送ack消息延迟确认消息失败");
        }
    }
}
