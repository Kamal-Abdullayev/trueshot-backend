package com.trueshot.comment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    private UUID id;

    private UUID postId;

    private UUID userId;

    private String content;

    private String url;

    private LocalDateTime createdAt;
}
