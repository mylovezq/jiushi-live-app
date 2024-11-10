package top.mylove7.live.user.provider.bank.service;


import top.mylove7.live.user.interfaces.bank.dto.AccountTradeRespDTO;

public interface IMyCurrencyAccountService {
    /**
     * 增加虚拟币
     *
     * @param userId
     * @param num
     */
    void incr(Long userId,Long num);

    /**
     * 扣减虚拟币
     *
     * @param userId
     * @param num
     */
    void decr(Long userId,Long num);

    /**
     * 查询余额
     *
     * @param userId
     * @return
     */
    Integer getBalanceByUserId(long userId);

    /**
     * 底层需要判断用户余额是否充足，充足则扣减，不足则拦截
     *
     * @param accountTradeReqDTO
     */
    AccountTradeRespDTO consume(top.mylove7.live.user.bank.dto.AccountTradeReqDTO accountTradeReqDTO);
}
