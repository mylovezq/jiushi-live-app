package top.mylove7.live.gateway.filter;


import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;

import top.mylove7.live.account.interfaces.im.ImTokenRpc;
import top.mylove7.live.common.interfaces.dto.ImUserInfoTokenDto;
import top.mylove7.live.common.interfaces.enums.GatewayHeaderEnum;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import top.mylove7.live.gateway.vo.GatewayRespVO;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static top.mylove7.live.common.interfaces.constants.ImCoreServerConstants.IM_WS_BIND_IP_KEY;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Component
@Slf4j
public class WebSocketFilter implements GlobalFilter, Ordered {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
        String scheme = requestUrl.getScheme();
        if (!"ws".equals(scheme) && !"wss".equals(scheme)) {
            return chain.filter(exchange);
        }
        List<String> token = exchange.getRequest().getQueryParams().get("token");
        ImUserInfoTokenDto userIdByToken = imTokenRpc.getUserIdByToken(token.get(0));
        if (userIdByToken == null){
            return GatewayRespVO.bizErr("请先登录im", UNAUTHORIZED.value(),exchange);
        }
        List<String> roomIdList = exchange.getRequest().getQueryParams().get("roomId");
        if (CollUtil.isNotEmpty(roomIdList)){
            String roomId = roomIdList.get(0);
            userIdByToken.setRoomId(Long.valueOf(roomId));
        }


        String redisIpAddr = stringRedisTemplate.opsForValue().get(IM_WS_BIND_IP_KEY + userIdByToken.getAppId() + ":" + userIdByToken.getUserId());
        if (redisIpAddr !=  null){
            log.info("用户id已经在此机器，就继续在此机器上，就不会 维护两套资源根据选择的机器 userId {}  ip addr:{}",userIdByToken,redisIpAddr);
            requestUrl= URI.create(scheme + "://" +redisIpAddr);
        }

        URI wsRequestUrl = UriComponentsBuilder.fromUri(requestUrl).scheme(scheme).build().toUri();
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, wsRequestUrl);

        // gateway --(header)--> springboot-web(interceptor-->get header)
        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
        builder.header(GatewayHeaderEnum.IM_USER_LOGIN_INFO.getName(), userIdByToken.toJson());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }

}
