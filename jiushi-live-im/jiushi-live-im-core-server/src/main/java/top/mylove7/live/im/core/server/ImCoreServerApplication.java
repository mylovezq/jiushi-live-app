package top.mylove7.live.im.core.server;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * netty启动类
 *
 * @Author jiushi
 *
 * @Description
 */
@SpringBootApplication
@EnableDubbo
public class ImCoreServerApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication springApplication = new SpringApplication(ImCoreServerApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(16));
        springApplication.run(args);
    }
}
