package top.mylove7.live.msg.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * @Author jiushi
 *
 * @Description
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
@Import(RocketMQAutoConfiguration.class)
public class MsgProviderApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MsgProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

}