package top.mylove7.jiushi.live.framework.mq.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author jiushi
 *
 * @Description
 */
@ConfigurationProperties(prefix = "jiushi.rmq.producer")
@Configuration
@Data
public class RocketMQProducerProperties {

    private String nameSrv;
    private String groupName;
    private String applicationName;
    private Integer sendMsgTimeout;
    private Integer retryTimes;

}
