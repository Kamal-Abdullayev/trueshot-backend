package com.trueshot.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentNotificationSendDto {
    private String commentId;
    private String postId;
    private String commentOwnerId;

}
