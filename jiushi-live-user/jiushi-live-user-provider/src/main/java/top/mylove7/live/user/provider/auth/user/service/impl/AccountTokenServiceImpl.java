package top.mylove7.live.user.provider.auth.user.service.impl;

import jakarta.annotation.Resource;
import top.mylove7.jiushi.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.user.provider.auth.user.service.IAccountTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class AccountTokenServiceImpl implements IAccountTokenService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public String createAndSaveLoginToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildUserLoginTokenKey(token), String.valueOf(userId), 30, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getUserIdByToken(String tokenKey) {
        String redisKey = cacheKeyBuilder.buildUserLoginTokenKey(tokenKey);
        Object userInfoStr = redisTemplate.opsForValue().get(redisKey);

        if (userInfoStr == null){
            throw new BizErrorException(403,"用户信息失效，请重新登录");
        }

        return Long.parseLong(userInfoStr+"") ;
    }
}
