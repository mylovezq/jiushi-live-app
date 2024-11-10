package top.mylove7.live.api.user.bank.service.impl;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import top.mylove7.live.api.user.bank.service.IBankService;

import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.user.interfaces.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.user.interfaces.bank.interfaces.IPayOrderRpc;
import top.mylove7.live.user.interfaces.bank.interfaces.IPayProductRpc;
import top.mylove7.live.user.interfaces.bank.qo.PayProductReqQo;
import top.mylove7.live.user.interfaces.bank.vo.PayProductRespVO;
import top.mylove7.live.user.interfaces.bank.vo.PayProductVO;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class BankServiceImpl implements IBankService {

    @DubboReference
    private IPayProductRpc payProductRpc;
    @DubboReference
    private ICurrencyAccountRpc currencyAccountRpc;
    @DubboReference
    private IPayOrderRpc payOrderRpc;
    @Resource
    private RestTemplate restTemplate;

    @Override
    public PayProductVO products(Integer type) {
        PayProductVO products = payProductRpc.products(type);
        Integer balance = currencyAccountRpc.getBalance(JiushiLoginRequestContext.getUserId());
        products.setCurrentBalance(balance);
        return products;
    }

    @Override
    public PayProductRespVO payProduct(PayProductReqQo payProductReqQo) {
        payProductReqQo.setUserId(JiushiLoginRequestContext.getUserId());
        return payOrderRpc.payProduct(payProductReqQo);
    }
}
