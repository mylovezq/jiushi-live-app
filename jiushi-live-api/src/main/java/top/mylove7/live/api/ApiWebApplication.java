package top.mylove7.live.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author jiushi
 *
 * @Description
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiWebApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ApiWebApplication.class);
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(16));
        springApplication.run(args);
    }
}
