package top.mylove7.live.im.core.server.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import top.mylove7.live.im.core.server.service.IRouterHandlerService;


import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class RouterHandlerRpcImpl implements IRouterHandlerRpc {

    @Resource
    private IRouterHandlerService routerHandlerService;

    @Override
    public void sendMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        routerHandlerService.onReceive(imMsgBodyInTcpWsDto);
    }

    @Override
    public void batchSendMsg(List<ImMsgBodyInTcpWsDto> imMsgBodyInTcpWsDtoList) {
        imMsgBodyInTcpWsDtoList.parallelStream().forEach(imMsgBody -> {
            routerHandlerService.onReceive(imMsgBody);
        });
    }
}
