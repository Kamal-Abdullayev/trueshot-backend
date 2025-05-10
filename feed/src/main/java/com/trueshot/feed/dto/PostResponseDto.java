package com.trueshot.feed.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {
    private String id;
    private String title;
    private String content;
    private String url;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}