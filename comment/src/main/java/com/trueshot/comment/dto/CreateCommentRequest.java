package com.trueshot.comment.dto;

import lombok.Data;


@Data
public class CreateCommentRequest {

    private String postId;
    private String content;
    private String imageContent; // ADD THIS (Base64 string)
}
