package top.mylove7.live.bank.interfaces;


import top.mylove7.live.bank.dto.PayOrderDTO;
import top.mylove7.live.bank.qo.PayProductReqQo;
import top.mylove7.live.bank.vo.PayProductRespVO;
import top.mylove7.live.bank.vo.WxPayNotifyQo;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IPayOrderRpc {

    /**
     * 插入订单
     *
     * @param payProductReqQo
     */
    PayProductRespVO payProduct(PayProductReqQo payProductReqQo);


    /**
     * 根据主键id做更新
     *
     * @param id
     * @param status
     */
    boolean updateOrderStatus(Long id,Integer status);

    /**
     * 根据订单id做更新
     *
     * @param orderId
     * @param status
     */
    boolean updateOrderStatus(String orderId,Integer status);


    /**
     * 支付回调需要请求该接口
     *
     * @param wxPayNotifyQo
     * @return
     */
    boolean payNotify(WxPayNotifyQo wxPayNotifyQo);
}
