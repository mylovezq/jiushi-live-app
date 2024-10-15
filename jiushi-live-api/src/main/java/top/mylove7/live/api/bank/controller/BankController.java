package top.mylove7.live.api.bank.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mylove7.live.api.bank.qo.PayProductReqQo;
import top.mylove7.live.api.bank.service.IBankService;
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

    @PostMapping("/products")
    public WebResponseVO products(Integer type) {
        ErrorAssert.isNotNull(type, BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(bankService.products(type));
    }

    // 1.申请调用第三方支付接口（签名-》支付宝/微信）(生成一条支付中状态的订单)
    // 2.生成一个（特定的支付页）二维码 （输入账户密码，支付）（第三方平台完成）
    // 3.发送回调请求 -》业务方
    // 要求(可以接收不同平台的回调数据)
    // 可以根据业务标识去回调不同的业务服务（自定义参数组成中，塞入一个业务code，根据业务code去回调不同的业务服务）

    @PostMapping("/payProduct")
    public WebResponseVO payProduct(PayProductReqQo payProductReqVO) {
        return WebResponseVO.success(bankService.payProduct(payProductReqVO));
    }

}
