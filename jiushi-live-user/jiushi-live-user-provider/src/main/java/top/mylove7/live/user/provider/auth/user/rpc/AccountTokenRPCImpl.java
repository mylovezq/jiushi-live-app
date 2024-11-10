package top.mylove7.live.user.provider.auth.user.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.user.interfaces.auth.interfaces.user.IAccountTokenRPC;
import top.mylove7.live.user.provider.auth.user.service.IAccountTokenService;


/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class AccountTokenRPCImpl implements IAccountTokenRPC {

    @Resource
    private IAccountTokenService accountTokenService;

    @Override
    public String createAndSaveLoginToken(Long userId) {
        return accountTokenService.createAndSaveLoginToken(userId);
    }

    @Override
    public Long getUserIdByToken(String tokenKey) {
        return accountTokenService.getUserIdByToken(tokenKey);
    }
}
