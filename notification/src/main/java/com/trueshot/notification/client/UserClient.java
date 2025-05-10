package com.trueshot.notification.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8087/api/v1/auth").build();
    }

    public List<String> getAllUserIds() {
        return webClient.get()
                .uri("/user/ids")
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .block();
    }
}
