package top.mylove7.live.api.user.bank.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import top.mylove7.live.api.user.bank.service.IPayNotifyService;

import org.springframework.stereotype.Service;
import top.mylove7.live.user.interfaces.bank.interfaces.IPayOrderRpc;
import top.mylove7.live.user.interfaces.bank.vo.WxPayNotifyQo;

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
