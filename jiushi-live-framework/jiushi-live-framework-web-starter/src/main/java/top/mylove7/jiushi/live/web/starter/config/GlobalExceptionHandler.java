package top.mylove7.jiushi.live.web.starter.config;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;


@ControllerAdvice
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {



    // 对特定异常进行捕获处理
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public WebResponseVO handleIllegalArgumentException(HttpServletRequest request , IllegalArgumentException ex) {
        log.error("请求路径：{}，概要错误信息==>{}" ,request.getRequestURI(),ex.getMessage());
        return WebResponseVO.bizError(ex.getMessage());
    }

    @ExceptionHandler(value = {BizErrorException.class})
    public WebResponseVO handleBizException(HttpServletRequest request, BizErrorException ex) {
        log.error("请求路径：{}，BizErrorException 概要错误信息==>{}" ,request.getRequestURI(),ex.getErrorMsg());
        return WebResponseVO.bizError(ex.getErrorCode(),ex.getErrorMsg());
    }

    @ExceptionHandler(value = {Exception.class})
    public WebResponseVO handleGeneralException(HttpServletRequest request, Exception ex) {
        log.error("请求路径：{}，Exception 概要错误信息==>{}" ,request.getRequestURI(),ex.getMessage());
        return WebResponseVO.bizError("系统异常："+ex.getMessage());
    }

    @ExceptionHandler(value = {RpcException.class})
    public WebResponseVO handleGeneralException(HttpServletRequest request, RpcException ex) {
        log.error("请求路径：{}，RpcException概要错误信息==>{}" ,request.getRequestURI(),ex.getMessage());
        if (ex.getMessage().contains("No provider available from registry")){
            return WebResponseVO.bizError("服务器火爆，请稍再试");
        }

        return WebResponseVO.bizError("服务调用异常："+ex.getMessage());
    }

    // 可以添加更多针对特定异常类型的处理方法
}

