package top.mylove7.live.api.user.bank.controller;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.*;
import top.mylove7.live.api.user.bank.service.IPayNotifyService;
import top.mylove7.live.user.interfaces.bank.vo.WxPayNotifyQo;


/**
 * 处理支付回调的逻辑
 *
 * @Author jiushi
 *
 * @Description
 */
@RestController
@RequestMapping("/payNotify")
public class PayNotifyController {

    @Resource
    private IPayNotifyService payNotifyService;

    @PostMapping("/wxNotify")
    public String wxNotify(@RequestBody WxPayNotifyQo wxPayNotifyQo) {
        return payNotifyService.notifyHandler(wxPayNotifyQo);
    }

}
