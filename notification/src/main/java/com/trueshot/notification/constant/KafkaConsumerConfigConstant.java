package com.trueshot.notification.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "spring.kafka.consumer")
@Component
public class KafkaConsumerConfigConstant {
    private String bootstrapServer;
    private String groupId;
    private String schemaRegistryUrl;
    private String jsonUseTypeHeaders;
    private String trustedPackages;
    private String jsonValueDefaultType;

}
