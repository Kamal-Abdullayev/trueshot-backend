package com.trueshot.challange.dto;


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


}
