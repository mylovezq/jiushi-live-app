package top.mylove7.live.msg.provider.service;



import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ImRouterService {


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
