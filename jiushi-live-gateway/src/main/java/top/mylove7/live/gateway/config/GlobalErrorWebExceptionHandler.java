package top.mylove7.live.gateway.config;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;

@Slf4j
@Order(-1)
@Configuration
@RequiredArgsConstructor
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {


    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        log.error("网关统一报错", ex);
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        // 设置返回值类型为json
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //设置返回编码
        response.setStatusCode(HttpStatus.OK);
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            WebResponseVO webResponseVO =  WebResponseVO.bizError("服务器异常：" + ex.getMessage());;
            if (ex instanceof BizErrorException){
                webResponseVO = WebResponseVO.bizError(((BizErrorException) ex).getErrorCode(), ex.getMessage());
            }
            if(ex != null && ex.getMessage().contains("Unable to find instance for")){
                webResponseVO = WebResponseVO.bizError( "服务器繁忙，请稍后再试");
            }

            return bufferFactory.wrap(JSONUtil.toJsonStr(webResponseVO).getBytes());
        }));
    }
}
