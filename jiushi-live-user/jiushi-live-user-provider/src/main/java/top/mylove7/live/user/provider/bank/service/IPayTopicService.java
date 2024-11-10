package top.mylove7.live.user.provider.bank.service;

import top.mylove7.live.user.provider.bank.dao.po.PayTopicPO;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IPayTopicService {

    /**
     * 根据code查询
     *
     * @param code
     * @return
     */
    PayTopicPO getByCode(Integer code);
}
