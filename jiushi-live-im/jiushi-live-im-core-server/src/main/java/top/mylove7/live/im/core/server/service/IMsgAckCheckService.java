package top.mylove7.live.im.core.server.service;

import org.redisson.api.RLock;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IMsgAckCheckService {

    /**
     * 主要是客户端发送ack包给到服务端后，调用进行ack记录的移除
     *
     * @param imMsgBodyInTcpWsDto
     */
    void doMsgAck(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto);

    /**
     * 发送延迟消息，用于进行消息重试功能
     *
     * @param imMsgBodyInTcpWsDto
     */
    void sendDelayMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto);

    RLock getImMsgSendLock(ImMsgBodyInTcpWsDto inTcpDto);
}
