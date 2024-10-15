package top.mylove7.live.api.user.service;

import jakarta.servlet.http.HttpServletResponse;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;


/**
 * @Author jiushi
 *
 * @Description
 */
public interface IUserLoginService {

    /**
     * 发送登录验证码
     *
     * @param phone
     * @return
     */
    WebResponseVO sendLoginCode(String phone);

    /**
     * 手机号+验证码登录
     *
     * @param phone
     * @param code
     * @return
     */
    WebResponseVO login(String phone, Integer code, HttpServletResponse response);
}
