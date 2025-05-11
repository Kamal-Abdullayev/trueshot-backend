package com.trueshot.post.dto;

import com.trueshot.post.entity.Post;
import com.trueshot.post.entity.Vote;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseDto {
    private String id;
    private String title;
    private String content;
    private String url;
    private String userId;
    private String challengeId;
    private Vote vote;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponseDto convert(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .url(post.getUrl())
                .vote(post.getVote())
                .userId(post.getUserId())
                .challengeId(post.getChallengeId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
