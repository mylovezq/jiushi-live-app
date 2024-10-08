package top.mylove7.live.account.provider.im.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.account.interfaces.im.ImOnlineRpc;
import top.mylove7.live.account.provider.im.service.ImOnlineService;


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
