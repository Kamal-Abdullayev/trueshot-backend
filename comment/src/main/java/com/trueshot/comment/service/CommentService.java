package com.trueshot.comment.service;

import com.trueshot.comment.constant.KafkaConfigConstant;
import com.trueshot.comment.dto.*;
import com.trueshot.comment.entity.Comment;
import com.trueshot.comment.repository.CommentRepository;
import com.trueshot.comment.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaConfigConstant configConstant;
    private final JwtService jwtService;


    public CommentDto createComment(CreateCommentRequest request, String authHeader) {
        String commentOwnerUserId = getUserIdFromToken(authHeader);


        if (request.getImageContent() == null || request.getImageContent().isBlank()) {
            throw new IllegalArgumentException("Image is required for comments.");
        }

        String imageUrl;

        Mono<MediaProcessUploadImageResponseDto> responseMono = webClient.post()
                .uri("http://localhost:8090/api/v1/image/upload")
                .bodyValue(new ImageSaveRequestDto(request.getImageContent(), "image"))
                .retrieve()
                .bodyToMono(MediaProcessUploadImageResponseDto.class);

        MediaProcessUploadImageResponseDto response = responseMono.block();
        if (response == null || response.imagePath() == null) {
            throw new IllegalStateException("Image upload failed.");
        }

        imageUrl = response.imagePath();

        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .userId(commentOwnerUserId)
                .content(request.getContent())
                .url(imageUrl)
                .createdAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);

        String postOwnerId = null;
        try {
            postOwnerId = webClient.get()
                    .uri("http://localhost:8086/api/v1/post/{postId}", request.getPostId())
                    .retrieve()
                    .bodyToMono(PostResponse.class)
                    .map(PostResponse::userId)
                    .block();
            log.info("Post owner ID: {}", postOwnerId);
        } catch (WebClientResponseException e) {
            log.error("Failed to fetch post owner: {}", e.getMessage());
        }

        if (postOwnerId != null && !postOwnerId.equals(commentOwnerUserId)) {
            NotificationDto notificationRequest = new NotificationDto(
                    postOwnerId,
                    "Someone commented on your post!"
            );

            log.info("Comment sending to Kafka topic: {}", configConstant.getPostPublishTopic());
            kafkaTemplate.send(configConstant.getPostPublishTopic(), notificationRequest);
            log.info("Comment sent to Kafka: {}", notificationRequest);

//            try {
//                webClient.post()
//                        .uri("http://localhost:8085/api/v1/notifications")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(notificationRequest)
//                        .retrieve()
//                        .toBodilessEntity()
//                        .block();
//            } catch (WebClientResponseException e) {
//                log.error("Failed to send notification: {}", e.getMessage());
//            }
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

    public record MediaProcessUploadImageRequestDto(String content, String type) {}
    public record MediaProcessUploadImageResponseDto(String imagePath) {}
    public record PostResponse(String id, String title, String content, String url, String userId) {}

    protected String getUserIdFromToken(String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return jwtService.extractUserId(token);
    }

}
