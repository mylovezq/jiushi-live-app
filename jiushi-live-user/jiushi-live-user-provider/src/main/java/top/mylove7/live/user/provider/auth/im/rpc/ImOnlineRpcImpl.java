package top.mylove7.live.user.provider.auth.im.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import top.mylove7.live.user.interfaces.auth.interfaces.im.ImOnlineRpc;
import top.mylove7.live.user.provider.auth.im.service.ImOnlineService;


/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class ImOnlineRpcImpl implements ImOnlineRpc {

    @Resource
    private ImOnlineService imOnlineService;

    @Override
    public boolean isOnline(long userId, int appId) {
        return imOnlineService.isOnline(userId,appId);
    }
}
