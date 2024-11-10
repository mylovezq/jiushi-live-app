package top.mylove7.live.user.provider.auth.im.service.impl;

import jakarta.annotation.Resource;

import top.mylove7.live.user.provider.auth.im.service.ImOnlineService;
import top.mylove7.live.common.interfaces.constants.ImCoreServerConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class ImOnlineServiceImpl implements ImOnlineService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean isOnline(long userId, int appId) {
        return redisTemplate.hasKey(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);
    }

}
