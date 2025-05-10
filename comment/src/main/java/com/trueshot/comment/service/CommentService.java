package com.trueshot.comment.service;

import com.trueshot.comment.dto.CommentDto;
import com.trueshot.comment.dto.CreateCommentRequest;
import com.trueshot.comment.dto.ImageSaveRequestDto;
import com.trueshot.comment.entity.Comment;
import com.trueshot.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

        private final CommentRepository commentRepository;
        private final WebClient webClient;

        public CommentDto createComment(CreateCommentRequest request, String userId) {
                if (request.getImageContent() == null || request.getImageContent().isBlank()) {
                        throw new IllegalArgumentException("Image is required for comments.");
                }

                String imageUrl = null;

                Mono<MediaProcessUploadImageResponseDto> responseMono = webClient.post()
                        .uri("http://localhost:8090/api/v1/image/upload")
                        .bodyValue(new ImageSaveRequestDto(
                                request.getImageContent(),
                                "image"
                        ))
                        .retrieve()
                        .bodyToMono(MediaProcessUploadImageResponseDto.class);

                MediaProcessUploadImageResponseDto response = responseMono.block();
                if (response == null || response.imagePath() == null) {
                        throw new IllegalStateException("Image upload failed");
                }

                imageUrl = response.imagePath();

                Comment comment = Comment.builder()
                        .postId(request.getPostId())
                        .userId(userId)
                        .content(request.getContent())
                        .url(imageUrl)
                        .createdAt(LocalDateTime.now())
                        .build();

                Comment saved = commentRepository.save(comment);

                String postOwnerId = null;
                try {
                        postOwnerId = webClient.get()
                                .uri("http://localhost:8086/api/v1/posts/{postId}", request.getPostId()) // Post service
                                .retrieve()
                                .bodyToMono(PostResponse.class)
                                .map(PostResponse::userId)
                                .block();
                        log.info("Post owner ID: {}", postOwnerId);
                } catch (WebClientResponseException e) {
                        log.error("Failed to fetch post owner: {}", e.getMessage());
                }

                if (postOwnerId != null && !postOwnerId.equals(userId)) {
                        NotificationRequest notificationRequest = new NotificationRequest(
                                postOwnerId,
                                "Someone interacted with your post!"
                        );

                        try {
                                webClient.post()
                                        .uri("http://localhost:8085/notifications") // Notification service
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(notificationRequest)
                                        .retrieve()
                                        .toBodilessEntity()
                                        .block();
                        } catch (WebClientResponseException e) {
                                log.error("Failed to send notification: {}", e.getMessage());
                        }
                }

                return CommentDto.builder()
                        .id(saved.getId())
                        .postId(saved.getPostId())
                        .userId(saved.getUserId())
                        .content(saved.getContent())
                        .url(imageUrl)
                        .createdAt(saved.getCreatedAt())
                        .build();
        }

        public List<CommentDto> getCommentsByPostId(String postId) {
                return commentRepository.findByPostId(postId).stream()
                        .map(comment -> CommentDto.builder()
                                .id(comment.getId())
                                .postId(comment.getPostId())
                                .userId(comment.getUserId())
                                .content(comment.getContent())
                                .url(comment.getUrl())
                                .createdAt(comment.getCreatedAt())
                                .build())
                        .collect(Collectors.toList());
        }

        public record MediaProcessUploadImageRequestDto(String content, String type) {
        }

        public record MediaProcessUploadImageResponseDto(String imagePath) {
        }

        public record PostResponse(String id, String title, String content, String url, String userId) {
        }

        public record NotificationRequest(String userId, String message) {
        }
}
