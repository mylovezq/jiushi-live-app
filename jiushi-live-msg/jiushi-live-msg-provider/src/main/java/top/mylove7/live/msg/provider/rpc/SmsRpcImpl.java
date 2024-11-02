package top.mylove7.live.msg.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.msg.dto.MsgCheckDTO;
import top.mylove7.live.msg.enums.MsgSendResultEnum;
import top.mylove7.live.msg.interfaces.ISmsRpc;
import top.mylove7.live.msg.provider.service.ISmsService;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class SmsRpcImpl implements ISmsRpc {

    @Resource
    private ISmsService smsService;

    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        return smsService.sendLoginCode(phone);
    }

    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        return smsService.checkLoginCode(phone,code);
    }

}