package com.trueshot.post.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8087")
                .build();
    }

    @Bean
    public WebClient mediaServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8090")
                .build();
    }
}
