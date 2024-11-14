package top.mylove7.live.im.core.server.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.im.core.server.interfaces.rpc.IImCoreRouterHandlerRpc;
import top.mylove7.live.im.core.server.service.IImSendMsgNettyService;


import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class ImCoreRouterHandlerRpcImpl implements IImCoreRouterHandlerRpc {

    @Resource
    private IImSendMsgNettyService imSendMsgNettyService;

    @Override
    public void sendMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        imSendMsgNettyService.onReceive(imMsgBodyInTcpWsDto);
    }

    @Override
    public boolean sendMsgToClient(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        return imSendMsgNettyService.sendMsgToClient(imMsgBodyInTcpWsDto);
    }

    @Override
    public void batchSendMsg(List<ImMsgBodyInTcpWsDto> imMsgBodyInTcpWsDtoList) {
        imMsgBodyInTcpWsDtoList.parallelStream().forEach(imMsgBody -> {
            imSendMsgNettyService.onReceive(imMsgBody);
        });
    }
}
