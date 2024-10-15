package top.mylove7.live.api.bank.controller;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.mylove7.live.api.bank.service.IPayNotifyService;

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
    public String wxNotify(@RequestParam("param") String param) {
        return payNotifyService.notifyHandler(param);
    }

}
