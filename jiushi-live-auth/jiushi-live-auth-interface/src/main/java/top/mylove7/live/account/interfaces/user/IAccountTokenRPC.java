package top.mylove7.live.account.interfaces.user;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IAccountTokenRPC {


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
