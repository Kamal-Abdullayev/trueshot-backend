package com.trueshot.feed.controller;

import com.trueshot.feed.dto.PostResponseDto;
import com.trueshot.feed.jwt.JwtService;
import com.trueshot.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getUserFeed(@RequestHeader("Authorization") String authHeader) {
        log.info("Authorization header: {}", authHeader);
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String userId = jwtService.extractUsername(token);
        return ResponseEntity.ok(feedService.getUserFeed(userId, token));
    }
}
