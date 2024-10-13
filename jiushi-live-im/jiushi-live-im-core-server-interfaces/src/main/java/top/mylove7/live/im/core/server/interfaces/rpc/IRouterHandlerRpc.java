package top.mylove7.live.im.core.server.interfaces.rpc;





import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;

import java.util.List;

/**
 * 专门给router层的服务进行调用的接口
 *
 * @Author jiushi
 *
 * @Description
 */
public interface IRouterHandlerRpc {

    /**
     * 按照用户id进行消息的发送
     *
     * @param imMsgBodyInTcpWsDto
     */
    void sendMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto);

    /**
     * 支持批量发送消息
     *
     * @param imMsgBodyInTcpWsDtoList
     */
    void batchSendMsg(List<ImMsgBodyInTcpWsDto> imMsgBodyInTcpWsDtoList);
}
