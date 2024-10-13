package top.mylove7.live.im.router.interfaces.rpc;




import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ImRouterRpc {


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
