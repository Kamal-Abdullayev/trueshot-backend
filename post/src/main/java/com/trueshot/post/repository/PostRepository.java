package com.trueshot.post.repository;

import com.trueshot.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface PostRepository extends JpaRepository<Post, String> {

    List<Post> findAllByUserIdIn(List<String> userIds);

}
