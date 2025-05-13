package com.trueshot.comment.controller;

import com.trueshot.comment.dto.CommentDto;
import com.trueshot.comment.dto.CreateCommentRequest;
import com.trueshot.comment.security.UserPrincipal;
import com.trueshot.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CreateCommentRequest request,
                                           @RequestHeader("Authorization") String authHeader) {

        return new ResponseEntity<>(commentService.createComment(request, authHeader), HttpStatus.CREATED);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPost(@PathVariable String postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
}
