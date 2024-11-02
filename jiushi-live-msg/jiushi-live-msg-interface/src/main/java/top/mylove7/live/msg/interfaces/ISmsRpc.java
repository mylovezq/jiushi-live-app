package top.mylove7.live.msg.interfaces;

import top.mylove7.live.msg.dto.MsgCheckDTO;
import top.mylove7.live.msg.enums.MsgSendResultEnum;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ISmsRpc {

    /**
     * 发送短信登录验证码接口
     *
     * @param phone
     * @return
     */
    MsgSendResultEnum sendLoginCode(String phone);

    /**
     * 校验登录验证码
     *
     * @param phone
     * @param code
     * @return
     */
    MsgCheckDTO checkLoginCode(String phone, Integer code);

}