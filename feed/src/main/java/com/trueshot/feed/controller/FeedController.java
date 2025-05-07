package com.trueshot.feed.controller;

import com.trueshot.feed.dto.PostResponseDto;
import com.trueshot.feed.jwt.JwtService;
import com.trueshot.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final JwtService jwtService;
    private final WebClient webClient;

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getUserFeed(@RequestHeader("Authorization") String authHeader) {
        log.info("Authorization header: {}", authHeader);
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String username = jwtService.extractUsername(token);
        
        // Get user ID from user service
        String userId = webClient.get()
                .uri("http://localhost:8087/api/v1/auth/user/id-by-username?username=" + username)
                .retrieve()
                .bodyToMono(String.class)
                .block();
                
        log.info("User ID for feed: {}", userId);
        return ResponseEntity.ok(feedService.getUserFeed(userId, token));
    }
}
