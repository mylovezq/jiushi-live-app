package top.mylove7.live.msg.provider.service;



import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;

import java.util.List;

/**
 * @Author jiushi
 * @Description
 * 根据消息发送的目标用户id，找出这些id所对应的主机地址，并将消息发送到对应的主机上
 *
 */
public interface MatchImCoreNettyMsgRouterService {


    /**
     * 发送消息
     *
     * @param imMsgBodyInTcpWsDto
     * @return
     */
    boolean sendMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto);

    /**
     * 批量发送消息，群聊场景
     *
     * @param imMsgBodyInTcpWsDto
     */
    void batchSendMsg(List<ImMsgBodyInTcpWsDto> imMsgBodyInTcpWsDto);
}
