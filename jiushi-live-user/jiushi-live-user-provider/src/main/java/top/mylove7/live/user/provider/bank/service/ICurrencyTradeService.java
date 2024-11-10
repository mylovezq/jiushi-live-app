package top.mylove7.live.user.provider.bank.service;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ICurrencyTradeService {

    /**
     * 插入一条流水记录
     *
     * @param userId
     * @param num
     * @param type
     * @return
     */
    boolean insertOne(Long userId,Long num,int type);
}
