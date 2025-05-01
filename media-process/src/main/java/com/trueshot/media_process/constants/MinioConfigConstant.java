package com.trueshot.media_process.constants;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "minio")
@Component
public class MinioConfigConstant {
    private String bucketName;
    private String url;
    private String accessKey;
    private String secretKey;


}
