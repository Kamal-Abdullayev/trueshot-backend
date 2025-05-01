package com.trueshot.media_process.config;

import com.trueshot.media_process.constants.MinioConfigConstant;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class MinioConfig {
    private final MinioConfigConstant minioConfigConstant;

    @Bean
    public MinioClient minioClient() {
        return new MinioClient.Builder()
                .endpoint(minioConfigConstant.getUrl())
                .credentials(minioConfigConstant.getAccessKey(), minioConfigConstant.getSecretKey())
                .build();
    }

}