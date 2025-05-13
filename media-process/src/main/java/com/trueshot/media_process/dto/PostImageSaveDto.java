package com.trueshot.media_process.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostImageSaveDto {
    private String postId;
    private String imageContent;
    private String imagePath;
}
