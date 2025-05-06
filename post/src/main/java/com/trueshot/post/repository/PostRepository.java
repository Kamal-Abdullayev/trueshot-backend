package com.trueshot.post.repository;

import com.trueshot.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, String> {
    Optional<List<Post>> findAllPostsByUserId(String userId, Pageable pageable);
    List<Post> findAllByUserIdIn(List<String> userIds);

}
