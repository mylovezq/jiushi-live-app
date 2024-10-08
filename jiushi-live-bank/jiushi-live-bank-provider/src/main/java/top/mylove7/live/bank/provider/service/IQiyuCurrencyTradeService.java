package top.mylove7.live.bank.provider.service;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IQiyuCurrencyTradeService {

    /**
     * 插入一条流水记录
     *
     * @param userId
     * @param num
     * @param type
     * @return
     */
    boolean insertOne(long userId,int num,int type);
}
