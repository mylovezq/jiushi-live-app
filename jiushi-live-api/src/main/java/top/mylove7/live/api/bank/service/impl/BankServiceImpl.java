package top.mylove7.live.api.bank.service.impl;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.mylove7.live.bank.qo.PayProductReqQo;
import top.mylove7.live.api.bank.service.IBankService;
import top.mylove7.live.bank.vo.PayProductRespVO;
import top.mylove7.live.bank.vo.PayProductVO;
import top.mylove7.live.bank.constants.OrderStatusEnum;
import top.mylove7.live.bank.constants.PaySourceEnum;
import top.mylove7.live.bank.dto.PayOrderDTO;
import top.mylove7.live.bank.dto.PayProductDTO;
import top.mylove7.live.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.bank.interfaces.IPayOrderRpc;
import top.mylove7.live.bank.interfaces.IPayProductRpc;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.error.BizBaseErrorEnum;
import top.mylove7.live.common.interfaces.error.ErrorAssert;

import java.util.HashMap;

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
