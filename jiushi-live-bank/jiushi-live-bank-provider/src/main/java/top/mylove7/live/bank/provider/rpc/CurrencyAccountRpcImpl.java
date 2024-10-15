package top.mylove7.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.bank.dto.AccountTradeReqDTO;
import top.mylove7.live.bank.dto.AccountTradeRespDTO;
import top.mylove7.live.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.bank.provider.service.ICurrencyAccountService;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class CurrencyAccountRpcImpl implements ICurrencyAccountRpc {

    @Resource
    private ICurrencyAccountService currencyAccountService;

    @Override
    public void incr(long userId, int num) {
        currencyAccountService.incr(userId, num);
    }

    @Override
    public void decr(long userId, int num) {
        currencyAccountService.decr(userId, num);
    }

    @Override
    public Integer getBalance(long userId) {
        return currencyAccountService.getBalance(userId);
    }

    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        return currencyAccountService.consumeForSendGift(accountTradeReqDTO);
    }

}
