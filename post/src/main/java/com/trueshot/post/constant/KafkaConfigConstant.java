package com.trueshot.post.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "spring.kafka")
@Configuration
public class KafkaConfigConstant {
    private String bootstrapServers;
    private String schemaRegistryUrl;
    private String requestTimeoutMs;
    private String maxBlockMs;
    private String postPublishTopic;
}
