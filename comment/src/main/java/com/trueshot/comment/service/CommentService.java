package com.trueshot.comment.service;

import com.trueshot.comment.dto.CommentDto;
import com.trueshot.comment.entity.Comment;
import com.trueshot.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentDto createComment(CommentDto commentDto) {
        Comment comment = Comment.builder()
                .postId(UUID.fromString(commentDto.getPostId()))
                .userId(UUID.fromString(commentDto.getUserId()))
                .content(commentDto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);

        return CommentDto.builder()
                .id(saved.getId().toString())
                .postId(saved.getPostId().toString())
                .userId(saved.getUserId().toString())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public List<CommentDto> getCommentsByPostId(String postId) {
        UUID postUUID = UUID.fromString(postId);
        return commentRepository.findByPostId(postUUID).stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId().toString())
                        .postId(comment.getPostId().toString())
                        .userId(comment.getUserId().toString())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public Optional<CommentDto> getCommentById(String id) {
        UUID uuid = UUID.fromString(id);
        return commentRepository.findById(uuid).map(comment -> CommentDto.builder()
                .id(comment.getId().toString())
                .postId(comment.getPostId().toString())
                .userId(comment.getUserId().toString())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build());
    }

    public void deleteComment(String id) {
        UUID uuid = UUID.fromString(id);
        commentRepository.deleteById(uuid);
    }
}
