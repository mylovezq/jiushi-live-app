package top.mylove7.live.api.service;

import top.mylove7.live.api.vo.req.PayProductReqVO;
import top.mylove7.live.api.vo.resp.PayProductRespVO;
import top.mylove7.live.api.vo.resp.PayProductVO;

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
     * @param payProductReqVO
     * @return
     */
    PayProductRespVO payProduct(PayProductReqVO payProductReqVO);
}
