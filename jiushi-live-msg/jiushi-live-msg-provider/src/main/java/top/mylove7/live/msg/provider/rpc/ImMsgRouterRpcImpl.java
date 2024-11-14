package top.mylove7.live.msg.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.msg.interfaces.ImMsgRouterRpc;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.msg.provider.service.MatchImCoreNettyMsgRouterService;


import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class ImMsgRouterRpcImpl implements ImMsgRouterRpc {

    @Resource
    private MatchImCoreNettyMsgRouterService routerService;


    @Override
    public boolean sendMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        return routerService.sendMsg(imMsgBodyInTcpWsDto);
    }

    @Override
    public void batchSendMsg(List<ImMsgBodyInTcpWsDto> imMsgBodyInTcpWsDtoList) {
        routerService.batchSendMsg(imMsgBodyInTcpWsDtoList);
    }
}
