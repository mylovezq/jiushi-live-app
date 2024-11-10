package top.mylove7.live.user.provider.auth.im.service;

/**
 * 判断用户是否在线service
 *
 * @Author jiushi
 *
 * @Description
 */
public interface ImOnlineService {

    /**
     * 判断用户是否在线
     *
     * @param userId
     * @param appId
     * @return
     */
    boolean isOnline(long userId,int appId);
}
