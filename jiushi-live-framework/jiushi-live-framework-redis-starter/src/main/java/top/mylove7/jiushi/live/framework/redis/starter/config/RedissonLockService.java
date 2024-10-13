package top.mylove7.jiushi.live.framework.redis.starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author dengmingyang
 * @since 2023/11/28 16:08
 */
@Component
@Slf4j
@Configuration
@EnableConfigurationProperties(RedisConfigureProperties.class)
public class RedissonLockService {
    @Resource
    private RedisConfigureProperties redisConfigureProperties;


    @Bean
    public RedissonClient redisson() {
        String redissonAddr = "redis://" + redisConfigureProperties.getHost() + ":" + redisConfigureProperties.getPort();
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec(new ObjectMapper()));
        config.useSingleServer().setAddress(redissonAddr).setPassword(redisConfigureProperties.getPassword());
        return Redisson.create(config);
    }
}