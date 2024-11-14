package top.mylove7.live.msg.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author jiushi
 *
 * @Description
 */
@ConfigurationProperties(prefix = "jiushi.sms.ccp")
@Configuration
@Data
public class SmsApplicationProperties {

    private String smsServerIp;
    private Integer port;
    private String accountSId;
    private String accountToken;
    private String appId;
    private String testPhone;


}
