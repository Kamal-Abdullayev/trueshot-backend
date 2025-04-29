package com.trueshot.feed.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FeedResponseDto {
    private Long id; // The ID of the feed.
    private String content; // The content of the feed.
    private Long userId; // The ID of the user who created the feed.
    private String userName; // The name of the user who created the feed.

    // Default constructor
    public FeedResponseDto() {}

    // Constructor to initialize feed response details
    public FeedResponseDto(Long id, String content, Long userId, String userName) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
    }
}
