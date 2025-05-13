package com.trueshot.post.service;

import com.trueshot.post.constant.KafkaConfigConstant;
import com.trueshot.post.dto.*;
import com.trueshot.post.entity.Post;
import com.trueshot.post.entity.Vote;
import com.trueshot.post.exception.ResourceNotFoundException;
import com.trueshot.post.jwt.JwtService;
import com.trueshot.post.repository.PostRepository;
import com.trueshot.post.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.InvalidParameterException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final WebClient mediaServiceWebClient;
    private final JwtService jwtService;
    private final WebClient userServiceWebClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaConfigConstant configConstant;
    private final VoteRepository voteRepository;


    public List<PostResponseDto> getAllPosts(Pageable pageable, String authHeader) {
        String username = getUsernameFromToken(authHeader);

        String userId = userServiceWebClient.get()
                .uri("/api/v1/auth/user/id-by-username?username=" + username)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Find all post by user ID: {}", userId);

        return postRepository.findAllPostsByUserIdOrderByCreatedAtDesc(userId, pageable).orElseThrow(
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
    public PostCreateResponseDto savePost(PostCreateRequestDto postCreateRequestDto) {
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

        log.info("Challenge ID: {}", postCreateRequestDto.getChallengeId());
        String challengeId = "0";
        if (postCreateRequestDto.getChallengeId() != null) {
            challengeId = postCreateRequestDto.getChallengeId();
        }

        log.info("Challenge ID will be: {}", challengeId);
        // Create and save the post
        Vote newVote = Vote.builder()
                .upVotes(0)
                .downVotes(0)
                .userIdsUpVoted(Set.of())
                .userIdsDownVoted(Set.of())
                .build();
        Vote vote = voteRepository.save(newVote);
        log.info("Vote saved to database: {}", vote);

        Post newPost = Post.builder()
                .title(postCreateRequestDto.getTitle())
                .content(postCreateRequestDto.getContent())
                .challengeId(challengeId)
                .url(response.getImagePath())
                .vote(vote)
                .userId(postCreateRequestDto.getUserId())
                .build();

        Post post = postRepository.save(newPost);
        log.info("Post saved to database: {}", post);

        PostChallengeSaveDto postChallengeSaveDto = PostChallengeSaveDto.builder()
                .postId(post.getId())
                .challengeId(challengeId)
                .build();

        kafkaTemplate.send(configConstant.getPostPublishTopic(), postChallengeSaveDto);
        log.info("Post with id {} published to {} ", post.getId(), configConstant.getPostPublishTopic());

        return PostCreateResponseDto.convert(post);
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
        List<Post> posts = postRepository.findAllByUserIdInOrderByCreatedAtDesc(userIds);
        return posts.stream()
                .map(PostResponseDto::convert)
                .toList();
    }

    public List<PostResponseDto> getPostsByChallengeId(String challengeId, Pageable pageable) {
        return postRepository.findAllByChallengeIdOrderByCreatedAtDesc(challengeId, pageable).orElseThrow(
                () -> new ResourceNotFoundException("Posts not found")
        ).stream()
                .map(PostResponseDto::convert)
                .toList();
    }

    @Transactional
    public Integer upVotePost(String postId, String authHeader) {
        Post post = getPostObjectById(postId);
        String userId = getUserIdFromToken(authHeader);

        Vote vote = post.getVote();
        if (vote == null) {
            log.info("Vote is null, creating a new one");
            vote = Vote.builder()
                    .upVotes(0)
                    .downVotes(0)
                    .userIdsUpVoted(Set.of())
                    .userIdsDownVoted(Set.of())
                    .build();        }
        log.info("Vote: " + vote);
        // Toggle off if already upvoted
        if (vote.getUserIdsUpVoted().contains(userId)) {
            vote.getUserIdsUpVoted().remove(userId);
            vote.setUpVotes(vote.getUpVotes() - 1);
            log.info("User {} removed upvote", userId);
        } else {
            // Remove downvote if exists
            if (vote.getUserIdsDownVoted().remove(userId)) {
                vote.setDownVotes(vote.getDownVotes() - 1);
                log.info("User {} removed downvote", userId);
            }
            vote.getUserIdsUpVoted().add(userId);
            vote.setUpVotes(vote.getUpVotes() + 1);
        }

        post.setVote(vote);
        voteRepository.save(vote);
        postRepository.save(post);

        return vote.getUpVotes();
    }

    @Transactional
    public Integer downVotePost(String postId, String authHeader) {
        Post post = getPostObjectById(postId);
        String userId = getUserIdFromToken(authHeader);

        Vote vote = post.getVote();
        if (vote == null) {
            vote = Vote.builder()
                    .upVotes(0)
                    .downVotes(0)
                    .userIdsUpVoted(Set.of())
                    .userIdsDownVoted(Set.of())
                    .build();
        }

        // Toggle off if already downvoted
        if (vote.getUserIdsDownVoted().contains(userId)) {
            vote.getUserIdsDownVoted().remove(userId);
            vote.setDownVotes(vote.getDownVotes() - 1);
        } else {
            // Remove upvote if exists
            if (vote.getUserIdsUpVoted().remove(userId)) {
                vote.setUpVotes(vote.getUpVotes() - 1);
            }
            vote.getUserIdsDownVoted().add(userId);
            vote.setDownVotes(vote.getDownVotes() + 1);
        }

        post.setVote(vote);
        voteRepository.save(vote);
        postRepository.save(post);

        return vote.getDownVotes();
    }

    public Vote getVotesByPostId(String postId) {
        Post post = getPostObjectById(postId);
        return post.getVote();
    }

    protected String getUsernameFromToken(String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return jwtService.extractUsername(token);
    }

    protected String getUserIdFromToken(String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return jwtService.extractUserId(token);
    }
}
