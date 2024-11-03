package top.mylove7.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import top.mylove7.live.bank.dto.PayOrderDTO;
import top.mylove7.live.bank.interfaces.IPayOrderRpc;
import top.mylove7.live.bank.provider.dao.po.PayOrderPO;
import top.mylove7.live.bank.provider.service.IPayOrderService;
import top.mylove7.live.bank.qo.PayProductReqQo;
import top.mylove7.live.bank.vo.PayProductRespVO;
import top.mylove7.live.bank.vo.WxPayNotifyQo;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;

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
