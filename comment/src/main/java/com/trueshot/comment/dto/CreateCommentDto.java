package com.trueshot.comment.dto;

import com.trueshot.comment.model.CommentReaction;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommentDto {

    private UUID postId;
    private CommentReaction reaction;
    private String imageContent;
}
