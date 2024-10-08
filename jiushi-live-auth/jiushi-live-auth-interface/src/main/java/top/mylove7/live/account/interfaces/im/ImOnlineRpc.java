package top.mylove7.live.account.interfaces.im;

/**
 * 判断用户是否在线rpc
 *
 * @Author jiushi
 *
 * @Description
 */
public interface ImOnlineRpc {

    /**
     * 判断用户是否在线
     *
     * @param userId
     * @param appId
     * @return
     */
    boolean isOnline(long userId,int appId);
}
