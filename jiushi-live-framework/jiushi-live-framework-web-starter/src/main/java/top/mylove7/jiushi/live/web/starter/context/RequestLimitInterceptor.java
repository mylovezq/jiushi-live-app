
package top.mylove7.jiushi.live.web.starter.context;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.jiushi.live.web.starter.config.RequestLimit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

/**
 * 对于重复请求，要有专门的拦截器去处理
 *
 * @Author jiushi
 *
 * @Description
 */
@Slf4j
public class RequestLimitInterceptor implements HandlerInterceptor {


    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.application.name}")
    private String applicationName;

    private static final DefaultRedisScript<Long> LIMIT_REQ_LUA = new DefaultRedisScript<>();
    static {

        LIMIT_REQ_LUA.setScriptText(
                "local key = KEYS[1]\n" +
                        "local limit = tonumber(ARGV[1])\n" +
                        "local second = tonumber(ARGV[2])\n" +
                        "local count = tonumber(redis.call('get', key))\n" +
                        "if count == nil then\n" +
                        "    redis.call('set', key, 1)\n" +
                        "    redis.call('expire', key, second)\n" +
                        "else\n" +
                        "    if count < limit then\n" +
                        "        redis.call('incrby', key, 1)\n" +
                        "    end\n" +
                        "end\n" +
                        "return count"
        );
        LIMIT_REQ_LUA.setResultType(Long.class);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  {
        try {
            if (handler instanceof HandlerMethod) {
                return limitReq(request, (HandlerMethod) handler);
            }
            return true;
        } catch (Exception e) {
            log.error("限流器异常", e);
            throw e;
        }
    }

    private boolean limitReq(HttpServletRequest request, HandlerMethod handler) {
        HandlerMethod handlerMethod = handler;
        boolean hasLimit = handlerMethod.getMethod().isAnnotationPresent(RequestLimit.class);
        if (hasLimit) {
            // 是否需要限制请求
            RequestLimit requestLimit = handlerMethod.getMethod().getAnnotation(RequestLimit.class);
            Long userId = JiushiLoginRequestContext.getUserId();

            String requestKey = applicationName + ":" + request.getRequestURI() + ":" + userId;
            int limit = requestLimit.limit();
            int second = requestLimit.second();

            // 使用 Lua 脚本实现原子操作
            Long reqTime = Optional.ofNullable(redisTemplate.execute(LIMIT_REQ_LUA, Arrays.asList(requestKey), limit, second)).orElse(0L);

            // 如果请求次数超过限制
            if (reqTime >= limit) {
                // 直接抛出全局异常，让异常捕获器处理
                log.error("[限速，请求过于频繁] userId is {}, req too much", userId);
                throw new BizErrorException(-1, requestLimit.msg());
            }

            log.info("限速器放行请求");
            return true;
        } else {
            return true;
        }
    }
}