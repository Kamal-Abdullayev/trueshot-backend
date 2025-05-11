package com.trueshot.post.controller;

import com.trueshot.post.dto.PostCreateRequestDto;
import com.trueshot.post.dto.PostCreateResponseDto;
import com.trueshot.post.dto.PostResponseDto;
import com.trueshot.post.dto.PostUpdateDto;
import com.trueshot.post.jwt.JwtService;
import com.trueshot.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@RestController
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;
    private final WebClient userServiceWebClient; // injected bean with base URL

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable String id) {
        return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
    }

    @GetMapping("/all")
        public ResponseEntity<List<PostResponseDto>> getAllPosts(@RequestParam(name = "page", defaultValue = "0") int page,
                                                             @RequestParam(name = "size", defaultValue = "5") int size,
                                                                 @RequestHeader("Authorization") String authHeader) {
        return new ResponseEntity<>(postService.getAllPosts(PageRequest.of(page, size), authHeader), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PostCreateResponseDto> createPost(@RequestBody PostCreateRequestDto post,
                                                            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String username = jwtService.extractUsername(token);

        String userId = userServiceWebClient.get()
                .uri("/api/v1/auth/user/id-by-username?username=" + username)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        post.setUserId(userId);
        return new ResponseEntity<>(postService.savePost(post), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable String id, @RequestBody PostUpdateDto post) {
        return new ResponseEntity<>(postService.updatePost(id, post), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable String id) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/by-user-ids")
    public ResponseEntity<List<PostResponseDto>> getPostsByUserIds(@RequestBody List<String> userIds) {
        return ResponseEntity.ok(postService.getPostsByUserIds(userIds));
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<PostResponseDto>> getPostsByChallengeId(@PathVariable String challengeId,
                                                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                                                        @RequestParam(name = "size", defaultValue = "5") int size) {
        return new ResponseEntity<>(postService.getPostsByChallengeId(challengeId, PageRequest.of(page, size)), HttpStatus.OK);
    }
}
