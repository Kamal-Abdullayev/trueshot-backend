package com.trueshot.feed.service;

import com.trueshot.feed.dto.PostResponseDto;
import com.trueshot.feed.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
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

        // 2. Create a list of user IDs including both followed users and the current user
        List<String> userIds = new ArrayList<>();
        userIds.add(userId); // Add current user's ID
        
        if (followedUsers != null && !followedUsers.isEmpty()) {
            // Add followed users' IDs
            followedUsers.stream()
                    .map(UserResponseDto::getId)
                    .forEach(userIds::add);
        }

        // 3. Fetch posts from Post microservice for all users
        return webClient.post()
                .uri("http://localhost:8086/api/v1/post/by-user-ids")
                .bodyValue(userIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PostResponseDto>>() {})
                .block();
    }
}
