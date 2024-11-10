package top.mylove7.live.api.user.bank.service;


import top.mylove7.live.user.interfaces.bank.vo.WxPayNotifyQo;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IPayNotifyService {


    String notifyHandler(WxPayNotifyQo wxPayNotifyQo);
}
