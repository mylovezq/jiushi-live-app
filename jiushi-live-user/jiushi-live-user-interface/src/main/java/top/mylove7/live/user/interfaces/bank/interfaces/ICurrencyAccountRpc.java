package top.mylove7.live.user.interfaces.bank.interfaces;

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
    void incr(Long userId,Long num);

    /**
     * 扣减虚拟币
     *
     * @param userId
     * @param num
     */
    void decrByRedis(Long userId,Long num);

    /**
     * 查询余额
     *
     * @param userId
     * @return
     */
    Integer getBalance(Long userId);


    void decrByDBAndRedis(Long userId, Long allNum);
}
