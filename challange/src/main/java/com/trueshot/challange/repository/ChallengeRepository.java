package com.trueshot.challange.repository;

import com.trueshot.challange.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChallengeRepository extends JpaRepository<Challenge, String> {
}
