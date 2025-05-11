package com.trueshot.challange.repository;

import com.trueshot.challange.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ChallengeRepository extends JpaRepository<Challenge, String> {
    Optional<Challenge> findChallengeById(String challengeId);
}
