package top.mylove7.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.bank.dto.AccountTradeReqDTO;
import top.mylove7.live.bank.dto.AccountTradeRespDTO;
import top.mylove7.live.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.bank.provider.service.IQiyuCurrencyAccountService;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class CurrencyAccountRpcImpl implements ICurrencyAccountRpc {

    @Resource
    private IQiyuCurrencyAccountService qiyuCurrencyAccountService;

    @Override
    public void incr(long userId, int num) {
        qiyuCurrencyAccountService.incr(userId, num);
    }

    @Override
    public void decr(long userId, int num) {
        qiyuCurrencyAccountService.decr(userId, num);
    }

    @Override
    public Integer getBalance(long userId) {
        return qiyuCurrencyAccountService.getBalance(userId);
    }

    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        return qiyuCurrencyAccountService.consumeForSendGift(accountTradeReqDTO);
    }

}
