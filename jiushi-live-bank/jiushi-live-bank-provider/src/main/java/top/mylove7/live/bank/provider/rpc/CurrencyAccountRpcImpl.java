package top.mylove7.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import top.mylove7.jiushi.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import top.mylove7.live.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.bank.provider.service.IMyCurrencyAccountService;
import top.mylove7.live.bank.provider.service.ICurrencyTradeService;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;

import java.util.Arrays;
import java.util.Collections;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
@Slf4j
public class CurrencyAccountRpcImpl implements ICurrencyAccountRpc {

    @Resource
    private IMyCurrencyAccountService myCurrencyAccountService;
    @Resource
    private ICurrencyTradeService currencyTradeService;
    @Resource
    private BankProviderCacheKeyBuilder bankProviderCacheKeyBuilder;
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
    public void incr(long userId, int num) {
        myCurrencyAccountService.incr(userId, num);
    }

    @Override
    public void decrByRedis(Long userId, int num) {
        RedisScript<String> deductionOfBalanceScript = new DefaultRedisScript<>(deductionOfBalance, String.class);
        String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(userId);
        // 执行 Lua 脚本
        String deductionOfBalanceResult = stringRedisTemplate.execute(deductionOfBalanceScript, Collections.singletonList(cacheKey), Arrays.asList(num+"", (60 * 60  * 12) +"").toArray());
        Assert.isTrue("成功".equals(deductionOfBalanceResult), deductionOfBalanceResult);

    }

    @Override
    public Integer getBalance(long userId) {
        return myCurrencyAccountService.getBalanceByUserId(userId);
    }


}
