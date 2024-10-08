package top.mylove7.live.account.interfaces.im;


import top.mylove7.live.common.interfaces.dto.ImUserInfoTokenDto;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ImTokenRpc {

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
