package top.mylove7.live.user.provider.bank.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import top.mylove7.live.user.interfaces.bank.interfaces.IPayOrderRpc;
import top.mylove7.live.user.interfaces.bank.qo.PayProductReqQo;
import top.mylove7.live.user.interfaces.bank.vo.PayProductRespVO;
import top.mylove7.live.user.interfaces.bank.vo.WxPayNotifyQo;
import top.mylove7.live.user.provider.bank.service.IPayOrderService;


/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class PayOrderRpcImpl implements IPayOrderRpc {

    @Resource
    private IPayOrderService payOrderService;


    @Override
    public PayProductRespVO payProduct(PayProductReqQo payProductReqQo) {
        return payOrderService.payProduct(payProductReqQo);
    }

    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        return payOrderService.updateOrderStatus(id, status);
    }

    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        return payOrderService.updateOrderStatus(orderId, status);
    }

    @Override
    public boolean payNotify(WxPayNotifyQo wxPayNotifyQo) {
        return payOrderService.payNotify(wxPayNotifyQo);
    }
}
