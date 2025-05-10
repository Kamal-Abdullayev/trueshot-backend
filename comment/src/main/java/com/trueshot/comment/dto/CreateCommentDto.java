package com.trueshot.comment.dto;

import com.trueshot.comment.model.CommentReaction;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommentDto {

    private String postId;
    private CommentReaction reaction;
    private String imageContent;
}
