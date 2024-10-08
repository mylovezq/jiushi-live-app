package top.mylove7.live.account.provider.user.service.impl;

import jakarta.annotation.Resource;
import top.mylove7.jiushi.live.framework.redis.starter.key.AccountProviderCacheKeyBuilder;
import top.mylove7.live.account.provider.user.service.IAccountTokenService;
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
    private AccountProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public String createAndSaveLoginToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildUserLoginTokenKey(token), String.valueOf(userId), 30, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getUserIdByToken(String tokenKey) {
        String redisKey = cacheKeyBuilder.buildUserLoginTokenKey(tokenKey);
        return (Long) redisTemplate.opsForValue().get(redisKey);
    }
}
