package ddog.notification.application.config.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String snsPaymentTopicArn;
}