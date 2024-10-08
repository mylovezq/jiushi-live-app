package top.mylove7.live.im.router.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.common.interfaces.dto.ImMsgBody;
import top.mylove7.live.im.router.interfaces.rpc.ImRouterRpc;
import top.mylove7.live.im.router.provider.service.ImRouterService;

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
    public boolean sendMsg(ImMsgBody imMsgBody) {
        return routerService.sendMsg(imMsgBody);
    }

    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        routerService.batchSendMsg(imMsgBodyList);
    }
}
