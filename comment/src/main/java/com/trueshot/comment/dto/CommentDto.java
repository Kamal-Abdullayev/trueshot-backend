package com.trueshot.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private String id;
    private String postId;
    private String userId;
    private String content;
    private LocalDateTime createdAt;

}
