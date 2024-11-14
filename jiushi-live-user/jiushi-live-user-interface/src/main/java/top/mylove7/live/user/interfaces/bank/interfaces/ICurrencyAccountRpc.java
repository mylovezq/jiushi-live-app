package top.mylove7.live.user.interfaces.bank.interfaces;

import top.mylove7.live.user.interfaces.bank.dto.BalanceMqDto;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ICurrencyAccountRpc {

    /**
     * 增加虚拟币
     *
     * @param userId
     * @param num
     */
    void incrBalance(BalanceMqDto balanceMqDto);
    void decrBalanceByDB(BalanceMqDto balanceMqDto);
    /**
     * 扣减虚拟币
     *
     * @param userId
     * @param num
     */
    void decrBalanceByRedis(Long userId,Long num);

    /**
     * 查询余额
     *
     * @param userId
     * @return
     */
    Integer getBalance(Long userId);



}
