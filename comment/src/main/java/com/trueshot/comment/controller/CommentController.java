package com.trueshot.comment.controller;

import com.trueshot.comment.dto.CreateCommentRequest;
import com.trueshot.comment.entity.Comment;
import com.trueshot.comment.repository.CommentRepository;
import com.trueshot.comment.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CreateCommentRequest request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        Comment comment = Comment.builder()
                .id(UUID.randomUUID()) // <== ADD THIS
                .postId(request.getPostId())
                .userId(userPrincipal.getId())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(savedComment);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable UUID postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return ResponseEntity.ok(comments);
    }
}
