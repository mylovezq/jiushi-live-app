package top.mylove7.live.gateway.filter;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import top.mylove7.live.gateway.properties.GatewayApplicationProperties;
import top.mylove7.live.common.interfaces.enums.GatewayHeaderEnum;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.mylove7.live.gateway.vo.GatewayRespVO;
import top.mylove7.live.user.interfaces.auth.interfaces.user.IAccountTokenRPC;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @Author jiushi
 *
 * @Description
 */
@Component
@Slf4j
public class AccountCheckFilter implements GlobalFilter, Ordered {

    @DubboReference
    private IAccountTokenRPC accountTokenRPC;

    private final PathMatcher pathMatcher = new AntPathMatcher();
    @Resource
    private GatewayApplicationProperties gatewayApplicationProperties;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求url，判断是否为空，如果为空则返回请求不通过
        ServerHttpRequest request = exchange.getRequest();
        String reqUrl = request.getURI().getPath();
        if (StrUtil.isBlank(reqUrl)) {
            return GatewayRespVO.bizErr("非法请求路径", BAD_REQUEST.value(), exchange);
        }
        //根据url，判断是否存在于url白名单中，如果存在，则不对token进行校验
        List<String> notCheckUrlList = gatewayApplicationProperties.getNotCheckUrlList();
        if (this.isPathMatched(reqUrl,notCheckUrlList)) {
            log.info("匹配到的白名单："+reqUrl);
            log.info("请求没不需要进行token校验，直接传达给业务下游");
            //直接将请求转给下游
            return chain.filter(exchange);
        }
        //如果不存在url白名单，从请求头中获取token
        List<String> authorization = request.getHeaders().get("Authorization");
        if (CollectionUtils.isEmpty(authorization) || StrUtil.isBlank(authorization.get(0))) {
            log.error("没有登录，没有请求token");
            return GatewayRespVO.bizErr("请先登录", UNAUTHORIZED.value(),exchange);
        }
        String jiushiTokenValue = authorization.get(0);
        //token获取到之后，调用rpc判断token是否合法，如果合法则吧token换取到的userId传递给到下游
        Long userId = accountTokenRPC.getUserIdByToken(jiushiTokenValue);
        //如果token不合法，则拦截请求，日志记录token失效
        if (userId == null) {
            log.error("请求的token失效了，被拦截");
            return GatewayRespVO.bizErr("登录已失效，请重新登录",401,exchange);
        }
        ServerHttpRequest.Builder builder = request.mutate();
        builder.header(GatewayHeaderEnum.USER_LOGIN_ID.getName(), String.valueOf(userId));
         return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }


    private boolean isPathMatched(String requestUrl, List<String> whitelistPaths) {
        String url = whitelistPaths.parallelStream().filter(whitelistPath -> pathMatcher.match(whitelistPath, requestUrl)).findFirst().orElse(null);
        return url != null;
    }
}
