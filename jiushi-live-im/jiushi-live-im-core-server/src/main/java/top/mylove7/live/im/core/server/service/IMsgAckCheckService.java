package top.mylove7.live.im.core.server.service;

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
     * 记录下消息的ack和times
     *
     * @param imMsgBodyInTcpWsDto
     * @param times
     */
    void recordMsgAck(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto, int times);

    /**
     * 发送延迟消息，用于进行消息重试功能
     *
     * @param imMsgBodyInTcpWsDto
     */
    void sendDelayMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto);

    /**
     * 获取ack消息的重试次数
     *
     * @param msgId
     * @param userId
     * @param appId
     * @return
     */
    Integer getMsgAckTimes(String msgId,Long userId,Long appId);

    boolean hadMsgAck(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) ;
}
