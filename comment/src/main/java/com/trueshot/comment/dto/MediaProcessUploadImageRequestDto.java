package com.trueshot.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaProcessUploadImageRequestDto {
    private String content;
    private String folder;
}
