package top.mylove7.live.account.provider.im.service;

import top.mylove7.live.common.interfaces.dto.ImUserInfoTokenDto;

/**
 * 用户登录token service
 *
 * @Author jiushi
 *
 * @Description
 */
public interface ImTokenService {

    /**
     * 创建用户登录im服务的token
     *
     * @param userId
     * @param appId
     * @return
     */
    String createImLoginToken(Long userId, Long appId);

    /**
     * 根据token检索用户id
     *
     * @param token
     * @return
     */
    ImUserInfoTokenDto getUserIdByToken(String token);
}
