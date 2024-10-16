package top.mylove7.live.bank.provider.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import top.mylove7.jiushi.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import top.mylove7.live.bank.constants.TradeTypeEnum;
import top.mylove7.live.bank.dto.AccountTradeReqDTO;
import top.mylove7.live.bank.dto.AccountTradeRespDTO;
import top.mylove7.live.bank.provider.dao.maper.ICurrencyAccountMapper;
import top.mylove7.live.bank.provider.dao.po.CurrencyAccountPO;
import top.mylove7.live.bank.provider.service.ICurrencyAccountService;
import top.mylove7.live.bank.provider.service.ICurrencyTradeService;
import top.mylove7.live.common.interfaces.error.BizErrorException;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static top.mylove7.live.common.interfaces.utils.ExecutorConfig.IO_EXECUTOR;

/**
 * @Author jiushi
 * @Description
 */
@Service
@Slf4j
public class JiushiCurrencyAccountServiceImpl implements ICurrencyAccountService {

    @Resource
    private ICurrencyAccountMapper currencyAccountMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private BankProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private ICurrencyTradeService currencyTradeService;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public boolean insertOne(long userId) {
        try {
            CurrencyAccountPO accountPO = new CurrencyAccountPO();
            accountPO.setUserId(userId);
            currencyAccountMapper.insert(accountPO);
            return true;
        } catch (Exception e) {
            log.info("账户余额插入数据失败", e);
            throw new BizErrorException("账户余额插入数据失败");
        }

    }

    private static final DefaultRedisScript<Boolean> INCREMENT_CUR_AND_EXPIRE = new DefaultRedisScript<>();

    static {
        INCREMENT_CUR_AND_EXPIRE.setScriptText(
                "local cacheKey = KEYS[1]\n" +
                        "local num = tonumber(ARGV[1])\n" +
                        "if redis.call('exists', cacheKey) == 1 then\n" +
                        "    redis.call('incrby', cacheKey, num)\n" +
                        "    redis.call('expire', cacheKey, 300)\n" +
                        "end"
        );
        INCREMENT_CUR_AND_EXPIRE.setResultType(Boolean.class);
    }

    /**
     * 充值的并发不会很高  可以直接走同步
     * @param userId
     * @param num
     */
    @Override
    public void incr(long userId, int num) {
        Assert.notNull(cacheKeyBuilder, "cacheKeyBuilder is null");
        String cacheKey = cacheKeyBuilder.buildUserBalance(userId);
        if (redisTemplate.hasKey(cacheKey)) {
            redisTemplate.opsForValue().increment(cacheKey, num);
            redisTemplate.expire(cacheKey, 5, TimeUnit.MINUTES);
        }
        this.consumeIncrDBHandler(userId, num);
        log.info("充值成功{}", num);


    }

    /**
     * 送礼物时，扣减余额，特别是送小礼物时，并发很高
     * @param userId
     * @param num
     */
    @Override
    public void decr(long userId, int num) {
        //扣减余额
        String cacheKey = cacheKeyBuilder.buildUserBalance(userId);
        if (redisTemplate.hasKey(cacheKey)) {
            //基于redis的扣减操作
            redisTemplate.opsForValue().decrement(cacheKey, num);
            redisTemplate.expire(cacheKey, 5, TimeUnit.MINUTES);
        }


        //TODO 可以使用投递到mq里面。削峰
        this.consumeDecrDBHandler(userId, num);
        log.info("消费扣减成功{}", num);


    }

    @Override
    public Integer getBalance(long userId) {
        String cacheKey = cacheKeyBuilder.buildUserBalance(userId);
        Object cacheBalance = redisTemplate.opsForValue().get(cacheKey);
        if (cacheBalance != null) {
            if ((Integer) cacheBalance == -1) {
                return null;
            }
            return (Integer) cacheBalance;
        }
        Integer currentBalance = currencyAccountMapper.queryBalance(userId);
        if (currentBalance == null) {
            redisTemplate.opsForValue().set(cacheKey, -1, 5, TimeUnit.MINUTES);
            return null;
        }
        redisTemplate.opsForValue().set(cacheKey, currentBalance, 2, TimeUnit.MINUTES);
        return currentBalance;
    }

    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        //余额判断
        long userId = accountTradeReqDTO.getUserId();
        int num = accountTradeReqDTO.getNum();
        Integer balance = this.getBalance(userId);
        if (balance == null || balance < num) {
            return AccountTradeRespDTO.buildFail(userId, "账户余额不足", 1);
        }
        this.decr(userId, num);
        return AccountTradeRespDTO.buildSuccess(userId, "消费成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public void consumeIncrDBHandler(long userId, int num) {
        CurrencyAccountPO currencyAccountUpdate = currencyAccountMapper.selectOne(Wrappers.<CurrencyAccountPO>lambdaQuery().eq(CurrencyAccountPO::getUserId, userId).last("for update"));
        if (currencyAccountUpdate == null) {
            currencyAccountUpdate = new CurrencyAccountPO();
            currencyAccountUpdate.setUserId(userId);
            currencyAccountUpdate.setCurrentBalance(num);
            currencyAccountUpdate.setTotalCharged(num);
            currencyAccountUpdate.setStatus(1);
            currencyAccountUpdate.setCreateTime(LocalDateTime.now());
            currencyAccountUpdate.setUpdateTime(LocalDateTime.now());
            currencyAccountMapper.insert(currencyAccountUpdate);
        } else {
            Assert.isTrue(currencyAccountUpdate.getStatus() == 1, "账户状态异常");
            //更新db，插入db
            if (!currencyAccountMapper.incr(userId, num)) {
                log.error("新增db异常，可能是账户状态异常");
                throw new BizErrorException("充值异常");
            }
        }
        //流水记录
        currencyTradeService.insertOne(userId, num, TradeTypeEnum.SEND_GIFT_TRADE.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void consumeDecrDBHandler(long userId, int num) {
        CurrencyAccountPO currencyAccountUpdate = currencyAccountMapper.selectOne(Wrappers.<CurrencyAccountPO>lambdaQuery().eq(CurrencyAccountPO::getUserId, userId).last("for update"));
        Assert.notNull(currencyAccountUpdate, "账户不存在");
        Assert.isTrue(currencyAccountUpdate.getStatus() == 1, "账户状态异常");
        Assert.isTrue(currencyAccountUpdate.getCurrentBalance() >= num, "余额不足");
        //更新db，插入db
        if (!currencyAccountMapper.decr(userId, num)) {
            log.info("扣减异常,可能使余额不足");
            throw new BizErrorException("扣减异常");
        }
        //流水记录
        currencyTradeService.insertOne(userId, num * -1, TradeTypeEnum.SEND_GIFT_TRADE.getCode());


    }


    @Override
    public AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO) {
//        long userId = accountTradeReqDTO.getUserId();
//        int num = accountTradeReqDTO.getNum();
//        //首先判断账户余额是否充足，考虑无记录的情况
//        QiyuCurrencyAccountDTO accountDTO = this.getByUserId(userId);
//        if (accountDTO == null) {
//            return AccountTradeRespDTO.buildFail(userId, "账户未有初始化", 1);
//        }
//        if (!accountDTO.getStatus().equals(CommonStatusEum.VALID_STATUS.getCode())) {
//            return AccountTradeRespDTO.buildFail(userId, "账号异常", 2);
//        }
//        if (accountDTO.getCurrentBalance() - num < 0) {
//            return AccountTradeRespDTO.buildFail(userId, "余额不足", 3);
//        }
        //todo 流水记录？
        //大并发请求场景，1000个直播间，500人，50w人在线，20%的人送礼，10w人在线触发送礼行为，
        //DB扛不住
        //1.MySQL换成写入性能相对较高的数据库
        //2.我们能不能从业务上去进行优化，用户送礼都在直播间，大家都连接上了im服务器，router层扩容（50台），im-core-server层（100台），RocketMQ削峰，
        // 消费端也可以水平扩容
        //3.我们客户端发起送礼行为的时候，同步校验（校验账户余额是否足够，余额放入到redis中），
        //4.拦截下大部分的请求，如果余额不足，（接口还得做防止重复点击，客户端也要防重复）
        //5.同步送礼接口，只完成简单的余额校验，发送mq，在mq的异步操作里面，完成二次余额校验，余额扣减，礼物发送
        //6.如果余额不足，是不是可以利用im，反向通知发送方
        // todo 性能问题
        //扣减余额
//        this.decr(userId, num);
        return AccountTradeRespDTO.buildSuccess(-1L, "扣费成功");
    }
}
