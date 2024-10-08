package top.mylove7.live.account.provider.user.service;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IAccountTokenService {

    /**
     * 创建一个登录token
     *
     * @param userId
     * @return
     */
    String createAndSaveLoginToken(Long userId);

    /**
     * 校验用户token
     *
     * @param tokenKey
     * @return
     */
    Long getUserIdByToken(String tokenKey);
}
