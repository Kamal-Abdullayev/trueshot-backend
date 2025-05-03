package com.trueshot.feed.service;

import com.trueshot.feed.dto.PostResponseDto;
import com.trueshot.feed.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final WebClient webClient;

    public List<PostResponseDto> getUserFeed(String userId, String token) {
        // 1. Fetch full UserResponseDto list
        List<UserResponseDto> followedUsers = webClient.get()
                .uri("http://localhost:8087/api/v1/follow/following")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserResponseDto>>() {})
                .block();

        if (followedUsers == null || followedUsers.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Extract just the UUIDs (not usernames)
        List<String> followedUserIds = followedUsers.stream()
                .map(UserResponseDto::getId)
                .toList();

        // 3. Fetch posts from Post microservice
        return webClient.post()
                .uri("http://localhost:8086/api/v1/post/by-user-ids")
                .bodyValue(followedUserIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PostResponseDto>>() {})
                .block();
    }
}
