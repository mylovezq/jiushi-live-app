package top.mylove7.live.api.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import top.mylove7.live.api.error.ApiErrorEnum;
import top.mylove7.live.api.service.IUserLoginService;
import top.mylove7.live.api.vo.UserLoginVO;
import org.apache.dubbo.config.annotation.DubboReference;
import top.mylove7.live.account.interfaces.user.IAccountTokenRPC;

import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;
import org.qiyu.live.msg.interfaces.ISmsRpc;
import top.mylove7.jiushi.live.web.starter.error.ErrorAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;
import top.mylove7.live.user.dto.UserLoginDTO;
import top.mylove7.live.user.interfaces.IUserPhoneRPC;

import java.util.regex.Pattern;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class UserLoginServiceImpl implements IUserLoginService {

    private static String PHONE_REG = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginServiceImpl.class);

    @DubboReference
    private ISmsRpc smsRpc;
    @DubboReference
    private IUserPhoneRPC userPhoneRPC;
    @DubboReference
    private IAccountTokenRPC accountTokenRPC;

    @Override
    public WebResponseVO sendLoginCode(String phone) {
        ErrorAssert.isNotBlank(phone, ApiErrorEnum.PHONE_IS_EMPTY);
        ErrorAssert.isTure(Pattern.matches(PHONE_REG, phone), ApiErrorEnum.PHONE_IN_VALID);
        MsgSendResultEnum msgSendResultEnum = smsRpc.sendLoginCode(phone);
        if (msgSendResultEnum == MsgSendResultEnum.SEND_SUCCESS) {
            return WebResponseVO.success();
        }
        return WebResponseVO.sysError("短信发送太频繁，请稍后再试");
    }

    @Override
    public WebResponseVO login(String phone, Integer code, HttpServletResponse response) {
        ErrorAssert.isNotBlank(phone, ApiErrorEnum.PHONE_IS_EMPTY);
        ErrorAssert.isTure(Pattern.matches(PHONE_REG, phone), ApiErrorEnum.PHONE_IN_VALID);
        ErrorAssert.isTure(code != null && code > 1000, ApiErrorEnum.SMS_CODE_ERROR);
        MsgCheckDTO msgCheckDTO = smsRpc.checkLoginCode(phone, code);
        if (!msgCheckDTO.isCheckStatus()) {
            return WebResponseVO.bizError(msgCheckDTO.getDesc());
        }
        //验证码校验通过
        UserLoginDTO userLoginDTO = userPhoneRPC.login(phone);
        ErrorAssert.isTure(userLoginDTO.isLoginSuccess(),ApiErrorEnum.USER_LOGIN_ERROR);

        String token = accountTokenRPC.createAndSaveLoginToken(userLoginDTO.getUserId());
        Cookie cookie = new Cookie("jiushiToken", token);
        //http://app.qiyu.live.com/html/qiyu_live_list_room.html
        //http://api.qiyu.live.com/live/api/userLogin/sendLoginCode
        cookie.setDomain("localhost");
        cookie.setPath("/");
        //cookie有效期，一般他的默认单位是秒
        cookie.setMaxAge(30 * 24 * 3600);
        //加上它，不然web浏览器不会将cookie自动记录下
        response.addCookie(cookie);
        return WebResponseVO.success(ConvertBeanUtils.convert(userLoginDTO, UserLoginVO.class));
    }
}
