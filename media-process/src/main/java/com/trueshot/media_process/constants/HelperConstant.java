package com.trueshot.media_process.constants;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "helper")
@Component
public class HelperConstant {
    private String appIp;
    private String baseUrl;
    private String imageFolderPath;

}
