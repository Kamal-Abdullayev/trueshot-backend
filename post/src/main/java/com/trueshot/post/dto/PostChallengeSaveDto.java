package com.trueshot.post.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostChallengeSaveDto {
    private String postId;
    private String challengeId;
}
