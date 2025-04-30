package com.trueshot.feed.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseDto {

    @JsonProperty("title")    // Explicitly add JsonProperty annotations (Optional)
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("url")
    private String url;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
