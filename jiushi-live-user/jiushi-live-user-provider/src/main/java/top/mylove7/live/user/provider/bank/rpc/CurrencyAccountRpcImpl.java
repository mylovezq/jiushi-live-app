package top.mylove7.live.user.provider.bank.rpc;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import top.mylove7.jiushi.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import top.mylove7.live.user.interfaces.bank.dto.BalanceMqDto;
import top.mylove7.live.user.interfaces.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.user.provider.bank.service.IMyCurrencyAccountService;

import java.util.Arrays;
import java.util.Collections;

/**
 * @Author jiushi
 * @Description
 */
@DubboService
@Slf4j
public class CurrencyAccountRpcImpl implements ICurrencyAccountRpc {

    @Resource
    private IMyCurrencyAccountService myCurrencyAccountService;

    @Resource
    private UserProviderCacheKeyBuilder bankProviderCacheKeyBuilder;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    // Lua 脚本
    String deductionOfBalance = "local cacheKey = KEYS[1]\n" +
            "local price = tonumber(ARGV[1])\n" +
            "local expireTime = tonumber(ARGV[2])\n" +
            "\n" +
            "local cacheBalance = redis.call('GET', cacheKey)\n" +
            "\n" +
            "if not cacheBalance then\n" +
            "    return '账户异常'\n" +
            "end\n" +
            "\n" +
            "cacheBalance = tonumber(cacheBalance)\n" +
            "\n" +
            "if cacheBalance == 0 or cacheBalance < price then\n" +
            "    return '余额不足'\n" +
            "end\n" +
            "\n" +
            "redis.call('DECRBY', cacheKey, price)\n" +
            "redis.call('EXPIRE', cacheKey, expireTime)\n" +
            "\n" +
            "return '成功'";



    @Override
    public void decrBalanceByRedis(Long userId, Long num) {
        RedisScript<String> deductionOfBalanceScript = new DefaultRedisScript<>(deductionOfBalance, String.class);
        String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(userId);
        // 执行 Lua 脚本
        String deductionOfBalanceResult = stringRedisTemplate.execute(deductionOfBalanceScript, Collections.singletonList(cacheKey), Arrays.asList(num + "", (60 * 60 * 12) + "").toArray());
        Assert.isTrue("成功".equals(deductionOfBalanceResult), deductionOfBalanceResult);

    }

    @Override
    public Integer getBalance(Long userId) {
        return myCurrencyAccountService.getBalanceByUserId(userId);
    }

    @Override
    public void incrBalance(BalanceMqDto balanceMqDto) {
        myCurrencyAccountService.incr(balanceMqDto);
    }

    @Override
    public void  decrBalanceByDB(BalanceMqDto balanceMqDto) {
        myCurrencyAccountService.decr(balanceMqDto);

    }


}
