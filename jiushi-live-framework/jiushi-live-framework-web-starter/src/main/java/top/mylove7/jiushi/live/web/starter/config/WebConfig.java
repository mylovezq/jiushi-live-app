package top.mylove7.jiushi.live.web.starter.config;

import top.mylove7.jiushi.live.web.starter.context.JiushiUserInfoInterceptor;
import top.mylove7.jiushi.live.web.starter.context.RequestLimitInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public JiushiUserInfoInterceptor qiyuUserInfoInterceptor() {
        return new JiushiUserInfoInterceptor();
    }

    @Bean
    public RequestLimitInterceptor requestLimitInterceptor(){
        return new RequestLimitInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(qiyuUserInfoInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");
        registry.addInterceptor(requestLimitInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");
    }

}
