package org.qiyu.live.im.core.server.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.common.interfaces.dto.ImMsgBody;
import org.qiyu.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.qiyu.live.im.core.server.service.IRouterHandlerService;


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
    public void sendMsg(ImMsgBody imMsgBody) {
        routerHandlerService.onReceive(imMsgBody);
    }

    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        imMsgBodyList.forEach(imMsgBody -> {
            routerHandlerService.onReceive(imMsgBody);
        });
    }
}
