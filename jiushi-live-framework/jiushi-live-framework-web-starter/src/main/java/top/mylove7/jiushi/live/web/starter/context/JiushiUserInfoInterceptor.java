package top.mylove7.jiushi.live.web.starter.context;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.enums.GatewayHeaderEnum;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static top.mylove7.live.common.interfaces.constants.RequestConstants.JIUSHI_USER_ID;

/**
 * 直播 用户信息拦截器
 *
 * @Author jiushi
 *
 * @Description
 */
public class JiushiUserInfoInterceptor implements HandlerInterceptor {

    //所有web请求来到这里的时候，都要被拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader(GatewayHeaderEnum.USER_LOGIN_ID.getName());

        //如果userId不为空，则把它放在线程本地变量里面去
        if (StrUtil.isNotBlank(userIdStr)) {
            JiushiLoginRequestContext.set(JIUSHI_USER_ID, Long.valueOf(userIdStr));
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        JiushiLoginRequestContext.clear();
    }
}
