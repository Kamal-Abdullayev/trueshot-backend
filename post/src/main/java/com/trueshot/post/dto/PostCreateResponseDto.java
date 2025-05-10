package com.trueshot.post.dto;

import com.trueshot.post.entity.Post;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCreateResponseDto {
    private String id;
    private String title;
    private String content;
    private String url;
    private String userId;

    public static PostCreateResponseDto convert(Post post) {
        return PostCreateResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .url(post.getUrl())
                .userId(post.getUserId())
                .build();
    }
}
