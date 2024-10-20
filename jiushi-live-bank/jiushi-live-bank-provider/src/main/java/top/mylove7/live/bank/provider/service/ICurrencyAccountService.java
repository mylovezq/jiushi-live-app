package top.mylove7.live.bank.provider.service;


import top.mylove7.live.bank.dto.AccountTradeReqDTO;
import top.mylove7.live.bank.dto.AccountTradeRespDTO;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ICurrencyAccountService {

    /**
     * 新增账户
     *
     * @param userId
     */
    boolean insertOne(long userId);

    /**
     * 增加虚拟币
     *
     * @param userId
     * @param num
     */
    void incr(long userId,int num);

    /**
     * 扣减虚拟币
     *
     * @param userId
     * @param num
     */
    void decr(long userId,int num);

    /**
     * 查询余额
     *
     * @param userId
     * @return
     */
    Integer getBalance(long userId);

    /**
     * 底层需要判断用户余额是否充足，充足则扣减，不足则拦截
     *
     * @param accountTradeReqDTO
     */
    AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO);
}
