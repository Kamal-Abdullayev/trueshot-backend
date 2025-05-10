package com.trueshot.comment.repository;

import com.trueshot.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {

    List<Comment> findByPostId(String postId);

}
