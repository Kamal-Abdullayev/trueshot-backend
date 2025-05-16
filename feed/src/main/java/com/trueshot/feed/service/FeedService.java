package com.trueshot.feed.service;

import com.trueshot.feed.dto.PostResponseDto;
import com.trueshot.feed.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private static final Logger log = LoggerFactory.getLogger(FeedService.class);
    private final WebClient webClient;

    public List<PostResponseDto> getUserFeed(String userId, String token) {
        // 1. Fetch the list of users the current user follows
        List<UserResponseDto> followedUsers = webClient.get()
                .uri("http://localhost:8090/api/v1/follow/following")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserResponseDto>>() {})
                .block();

        // 2. Build the combined userId list (self + follows)
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        if (followedUsers != null) {
            followedUsers.stream()
                    .map(UserResponseDto::getId)
                    .forEach(userIds::add);
        }

        // 3. Fetch all posts for these users
        List<PostResponseDto> posts = webClient.post()
                .uri("http://localhost:8090/api/v1/post/by-user-ids")
                .bodyValue(userIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PostResponseDto>>() {})
                .block();

        // —— LOG RAW POSTS ——
        log.info("Raw posts from post-service (count={}):", posts == null ? 0 : posts.size());
        if (posts != null) {
            for (PostResponseDto p : posts) {
                log.info("  id={} challengeId={}", p.getId(), p.getChallengeId());
            }
        }

        if (posts == null || posts.isEmpty()) {
            return List.of();
        }

        // 4. Filter out any posts that belong to a challenge
        List<PostResponseDto> filtered = posts.stream()
                .filter(p -> p.getChallengeId() == null || "0".equals(p.getChallengeId()))
                .toList();

        log.info("Filtered posts for feed (count={}):", filtered.size());
        return filtered;
    }
}
