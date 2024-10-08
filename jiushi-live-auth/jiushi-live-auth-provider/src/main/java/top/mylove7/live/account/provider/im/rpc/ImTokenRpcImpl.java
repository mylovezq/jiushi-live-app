package top.mylove7.live.account.provider.im.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import top.mylove7.live.common.interfaces.dto.ImUserInfoTokenDto;
import top.mylove7.live.account.interfaces.im.ImTokenRpc;
import top.mylove7.live.account.provider.im.service.ImTokenService;

/**
 * 用户登录token rpc
 *
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class ImTokenRpcImpl implements ImTokenRpc {

    @Resource
    private ImTokenService imTokenService;

    @Override
    public String createImLoginToken(Long userId, Long appId) {
        return imTokenService.createImLoginToken(userId,appId);
    }

    @Override
    public ImUserInfoTokenDto getUserIdByToken(String token) {
        return imTokenService.getUserIdByToken(token);
    }
}
