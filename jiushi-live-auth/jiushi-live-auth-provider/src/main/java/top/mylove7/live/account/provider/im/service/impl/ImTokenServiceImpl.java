package top.mylove7.live.account.provider.im.service.impl;

import jakarta.annotation.Resource;
import top.mylove7.jiushi.live.framework.redis.starter.key.AccountProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.dto.ImUserInfoTokenDto;
import top.mylove7.live.account.provider.im.service.ImTokenService;
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
public class ImTokenServiceImpl implements ImTokenService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private AccountProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public String createImLoginToken(Long userId, Long appId) {
        String token = UUID.randomUUID() +"";
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildImLoginTokenKey(token), new ImUserInfoTokenDto(userId, appId), 5, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public ImUserInfoTokenDto getUserIdByToken(String token) {
        return (ImUserInfoTokenDto) redisTemplate.opsForValue().get(cacheKeyBuilder.buildImLoginTokenKey(token));
    }

}
