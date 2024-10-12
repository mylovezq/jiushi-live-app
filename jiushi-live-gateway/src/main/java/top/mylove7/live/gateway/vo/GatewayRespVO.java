package top.mylove7.live.gateway.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;

@Data
public class GatewayRespVO {
    @SneakyThrows
    public static Mono<Void> bizErr(String msg, int code, ServerWebExchange exchange){
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] bytes = new ObjectMapper().writeValueAsBytes(WebResponseVO.bizError(code,msg));
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
