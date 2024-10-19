package top.mylove7.jiushi.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author jiushi
 *
 * @Description
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class ImCoreServerProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String IM_ONLINE_ZSET = "imOnlineZset";
    private static String HAD_SEND_IM_MSG = "hadSendImMsg";
    private static String IM_ACK_MAP = "imAckMap";

    public String buildImAckMapKey(Long userId,Long appId) {
        return super.getPrefix() + IM_ACK_MAP + super.getSplitItem() + appId + super.getSplitItem() + userId % 100;
    }

    /**
     * 按照用户id取模10000，得出具体缓存所在的key
     *
     * @param userId
     * @return
     */
    public String buildImLoginTokenKey(Long userId, Long appId) {
        return super.getPrefix() + IM_ONLINE_ZSET + super.getSplitItem() + appId + super.getSplitItem() + userId % 10000;
    }

    public String buildHadSendMsgKey(Long appId, Long roomId,Long toUserId) {
        return super.getPrefix() + HAD_SEND_IM_MSG + super.getSplitItem() + appId + super.getSplitItem()  + roomId + super.getSplitItem() + toUserId;
    }

}
