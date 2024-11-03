package top.mylove7.live.api.bank.service;

import top.mylove7.live.bank.vo.WxPayNotifyQo;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IPayNotifyService {


    String notifyHandler(WxPayNotifyQo wxPayNotifyQo);
}
