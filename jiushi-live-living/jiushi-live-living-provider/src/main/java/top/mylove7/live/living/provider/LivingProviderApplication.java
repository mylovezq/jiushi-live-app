package top.mylove7.live.living.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author jiushi
 *
 * @Description
 */
@SpringBootApplication
@EnableDubbo
public class LivingProviderApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(LivingProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
