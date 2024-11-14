package top.mylove7.live.im.core.server.service;


import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IImSendMsgNettyService {

    /**
     * 当收到业务服务的请求，进行处理
     *
     * @param imMsgBodyInTcpWsDto
     */
    void onReceive(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto);


    /**
     * 发送消息给客户端
     *
     * @param imMsgBodyInTcpWsDto
     */
    boolean sendMsgToClient(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto);
}
