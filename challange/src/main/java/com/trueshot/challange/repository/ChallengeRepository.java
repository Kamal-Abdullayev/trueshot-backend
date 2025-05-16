package com.trueshot.challange.repository;

import com.trueshot.challange.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;


//public interface ChallengeRepository extends JpaRepository<Challenge, String> {
//    Optional<Challenge> findChallengeById(String challengeId);
//}


public interface ChallengeRepository extends JpaRepository<Challenge, String> {
    Optional<Challenge> findChallengeById(String challengeId);

    /**
     * Find all challenges whose endTime has passed and which haven’t yet had their rewards assigned.
     */
    List<Challenge> findByEndTimeBeforeAndRewardAssignedFalse(LocalDateTime time);
}

