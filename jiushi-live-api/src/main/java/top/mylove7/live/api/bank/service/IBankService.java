package top.mylove7.live.api.bank.service;

import top.mylove7.live.api.bank.PayProductReqQo;
import top.mylove7.live.bank.vo.PayProductRespVO;
import top.mylove7.live.bank.vo.PayProductVO;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IBankService {

    /**
     * 查询相关的产品列表信息
     *
     * @param type
     * @return
     */
    PayProductVO products(Integer type);

    /**
     * 发起支付
     *
     * @param payProductReqQo
     * @return
     */
    PayProductRespVO payProduct(PayProductReqQo payProductReqQo);
}
