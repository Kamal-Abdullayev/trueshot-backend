package com.trueshot.post.service;

import com.trueshot.post.dto.*;
import com.trueshot.post.entity.Post;
import com.trueshot.post.exception.ResourceNotFoundException;
import com.trueshot.post.jwt.JwtService;
import com.trueshot.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.InvalidParameterException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final WebClient mediaServiceWebClient; // renamed to use the correct bean
    private final JwtService jwtService;
    private final WebClient userServiceWebClient; // injected bean with base URL

    public List<PostResponseDto> getAllPosts(Pageable pageable, String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        log.debug("Token: {}", token);
        String username = jwtService.extractUsername(token);

        String userId = userServiceWebClient.get()
                .uri("/api/v1/auth/user/id-by-username?username=" + username)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Find all post by user ID: {}", userId);

        return postRepository.findAllPostsByUserId(userId, pageable).orElseThrow(
                () -> new ResourceNotFoundException("Posts not found")
                ).stream()
                .map(PostResponseDto::convert)
                .toList();
    }

    public PostResponseDto getPostById(String postId) {
        return PostResponseDto.convert(getPostObjectById(postId));
    }

    public Post getPostObjectById(String postId) {
        if (postId == null || postId.isEmpty()) {
            throw new InvalidParameterException("Invalid post id");
        }
        return postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post not found")
        );
    }

    @Transactional
    public PostResponseDto savePost(PostCreateRequestDto postCreateRequestDto) {
        // Upload the image to the media service
        Mono<MediaProcessUploadImageResponseDto> responseMono = mediaServiceWebClient.post()
                .uri("/api/v1/image/upload")
                .bodyValue(new MediaProcessUploadImageRequestDto(
                        postCreateRequestDto.getImageContent(),
                        "image"
                ))
                .retrieve()
                .bodyToMono(MediaProcessUploadImageResponseDto.class);

        MediaProcessUploadImageResponseDto response = responseMono.block();
        if (response == null || response.getImagePath() == null) {
            throw new ResourceNotFoundException("Image not found");
        }

        // Create and save the post
        Post post = Post.builder()
                .title(postCreateRequestDto.getTitle())
                .content(postCreateRequestDto.getContent())
                .url(response.getImagePath())
                .userId(postCreateRequestDto.getUserId())
                .build();

        return PostResponseDto.convert(postRepository.save(post));
    }

    public PostResponseDto updatePost(String postId, PostUpdateDto postUpdateDto) {
        Post dbPost = getPostObjectById(postId);

        if (postUpdateDto.getTitle() != null && !postUpdateDto.getTitle().equals(dbPost.getTitle())) {
            dbPost.setTitle(postUpdateDto.getTitle());
        }
        if (postUpdateDto.getContent() != null && !postUpdateDto.getContent().equals(dbPost.getContent())) {
            dbPost.setContent(postUpdateDto.getContent());
        }
        if (postUpdateDto.getUrl() != null && !postUpdateDto.getUrl().equals(dbPost.getUrl())) {
            dbPost.setUrl(postUpdateDto.getUrl());
        }

        return PostResponseDto.convert(postRepository.save(dbPost));
    }

    public void deletePost(String postId) {
        if (postId == null || postId.isEmpty()) {
            throw new InvalidParameterException("Invalid post id");
        }
        postRepository.deleteById(postId);
    }

    public List<PostResponseDto> getPostsByUserIds(List<String> userIds) {
        List<Post> posts = postRepository.findAllByUserIdIn(userIds);
        return posts.stream()
                .map(PostResponseDto::convert)
                .toList();
    }
}
