package com.trueshot.comment.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateCommentRequest {

    private UUID postId;
    private String content;
    private String imageContent; // ADD THIS (Base64 string)
}
