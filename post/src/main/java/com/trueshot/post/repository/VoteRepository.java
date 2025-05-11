package com.trueshot.post.repository;

import com.trueshot.post.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, String> {

}
