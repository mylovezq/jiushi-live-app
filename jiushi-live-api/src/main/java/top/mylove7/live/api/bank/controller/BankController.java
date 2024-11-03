package top.mylove7.live.api.bank.controller;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.mylove7.live.bank.qo.PayProductReqQo;
import top.mylove7.live.api.bank.service.IBankService;
import top.mylove7.live.bank.interfaces.ICurrencyAccountRpc;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.error.BizBaseErrorEnum;
import top.mylove7.live.common.interfaces.error.ErrorAssert;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;


/**
 * @Author jiushi
 *
 * @Description
 */
@RestController
@RequestMapping("/bank")
public class BankController {

    @Resource
    private IBankService bankService;

    @DubboReference
    private ICurrencyAccountRpc currencyAccountRpc;


    @PostMapping("/products")
    public WebResponseVO products(@RequestParam Integer type) {
        return WebResponseVO.success(bankService.products(type));
    }

    // 1.申请调用第三方支付接口（签名-》支付宝/微信）(生成一条支付中状态的订单)
    // 2.生成一个（特定的支付页）二维码 （输入账户密码，支付）（第三方平台完成）
    // 3.发送回调请求 -》业务方
    // 要求(可以接收不同平台的回调数据)
    // 可以根据业务标识去回调不同的业务服务（自定义参数组成中，塞入一个业务code，根据业务code去回调不同的业务服务）

    @PostMapping("/payProduct")
    public WebResponseVO payProduct(@RequestBody @Validated PayProductReqQo payProductReqVO) {
        return WebResponseVO.success(bankService.payProduct(payProductReqVO));
    }

    @PostMapping("incr")
    public WebResponseVO incr() {
        currencyAccountRpc.incr(JiushiLoginRequestContext.getUserId(),2);
        return WebResponseVO.success();
    }


}
