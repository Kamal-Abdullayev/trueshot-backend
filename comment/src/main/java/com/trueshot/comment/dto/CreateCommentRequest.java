package com.trueshot.comment.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateCommentRequest {

    private UUID postId;
    private UUID userId;
    private String content;
}
