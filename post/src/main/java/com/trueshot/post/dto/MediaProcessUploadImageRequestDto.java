package com.trueshot.post.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaProcessUploadImageRequestDto {
    private String imageContent;
    private String imageName;
}
