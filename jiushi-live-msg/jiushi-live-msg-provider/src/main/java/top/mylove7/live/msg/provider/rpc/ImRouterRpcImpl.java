package top.mylove7.live.msg.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.msg.interfaces.ImRouterRpc;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.msg.provider.service.ImRouterService;


import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class ImRouterRpcImpl implements ImRouterRpc {

    @Resource
    private ImRouterService routerService;


    @Override
    public boolean sendMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        return routerService.sendMsg(imMsgBodyInTcpWsDto);
    }

    @Override
    public void batchSendMsg(List<ImMsgBodyInTcpWsDto> imMsgBodyInTcpWsDtoList) {
        routerService.batchSendMsg(imMsgBodyInTcpWsDtoList);
    }
}
