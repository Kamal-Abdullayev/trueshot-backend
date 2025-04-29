package com.trueshot.feed.controller;

import com.trueshot.feed.dto.PostResponseDto;
import com.trueshot.feed.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feeds")
public class FeedController {

    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    // Endpoint to get all posts in the feed
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getFeed() {
        List<PostResponseDto> posts = feedService.getFeed();
        return ResponseEntity.ok(posts);  // Return list of PostResponseDto
    }
}
