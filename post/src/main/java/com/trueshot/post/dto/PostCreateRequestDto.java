package com.trueshot.post.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCreateRequestDto {
    private String title;
    private String content;
    private String imageContent;
    private String challengeId;
    private String userId;

}
