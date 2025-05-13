package com.trueshot.feed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {
    private String id;
    private String title;
    private String content;
    private String url;
    private String challengeId;
    private String userId;
    @JsonProperty("vote")
    private VoteResponseDto voteResponseDto;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}