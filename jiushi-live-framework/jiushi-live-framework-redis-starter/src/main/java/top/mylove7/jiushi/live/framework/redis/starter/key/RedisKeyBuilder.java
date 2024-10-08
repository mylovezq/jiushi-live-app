package top.mylove7.jiushi.live.framework.redis.starter.key;

import org.springframework.beans.factory.annotation.Value;

/**
 * @Author jiushi
 *
 * @Description
 */
public class RedisKeyBuilder {

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String SPLIT_ITEM = ":";

    public String getSplitItem() {
        return SPLIT_ITEM;
    }

    public String getPrefix() {
        return applicationName + SPLIT_ITEM;
    }
}