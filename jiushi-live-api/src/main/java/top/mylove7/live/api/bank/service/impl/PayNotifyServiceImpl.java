package top.mylove7.live.api.bank.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import top.mylove7.live.api.bank.service.IPayNotifyService;
import top.mylove7.live.bank.vo.WxPayNotifyQo;
import top.mylove7.live.bank.dto.PayOrderDTO;
import top.mylove7.live.bank.interfaces.IPayOrderRpc;
import org.springframework.stereotype.Service;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class PayNotifyServiceImpl implements IPayNotifyService {

    @DubboReference
    private IPayOrderRpc payOrderRpc;

    @Override
    public String notifyHandler(WxPayNotifyQo wxPayNotifyQo) {
        return payOrderRpc.payNotify(wxPayNotifyQo) ? "success" : "fail";
    }
}
