package top.mylove7.jiushi.live.framework.mq.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author jiushi
 *
 * @Description
 */
@ConfigurationProperties(prefix = "jiushi.rmq.consumer")
@Configuration
@Data
public class RocketMQConsumerProperties {

    private String nameSrv;
    private String groupName;
    private Integer consumeTimeout;

}
