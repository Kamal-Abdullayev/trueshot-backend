package com.trueshot.feed.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FeedRequestDto {
    // Getter and Setter for content
    private String content; // The content of the feed (could be a post, image, etc.).
    // Getter and Setter for userId
    private Long userId; // The ID of the user who is creating the feed.

    // Default constructor
    public FeedRequestDto() {}

    // Constructor to initialize content and userId
    public FeedRequestDto(String content, Long userId) {
        this.content = content;
        this.userId = userId;
    }

}
