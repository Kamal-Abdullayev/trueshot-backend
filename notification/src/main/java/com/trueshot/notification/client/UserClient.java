package com.trueshot.notification.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8087/api/v1/auth").build();
    }

    public List<UUID> getAllUserIds() {
        return webClient.get()
                .uri("/user/ids")
                .retrieve()
                .bodyToFlux(UUID.class)
                .collectList()
                .block();
    }
}
