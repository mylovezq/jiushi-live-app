package top.mylove7.live.msg.interfaces;






import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ImMsgRouterRpc {


    /**
     * 发送消息
     *
     * @param imMsgBodyInTcpWsDto
     * @return
     */
    boolean sendMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto);


    /**
     * 批量发送消息，在直播间内
     *
     * @param imMsgBodyInTcpWsDto
     */
    void batchSendMsg(List<ImMsgBodyInTcpWsDto> imMsgBodyInTcpWsDto);
}
