package com.trueshot.challange.service;

import com.trueshot.challange.entity.Challenge;
import com.trueshot.challange.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    public Challenge createChallenge(String content, UUID groupId, UUID adminId, Set<UUID> memberIds) {
        Challenge challenge = Challenge.builder()
                .content(content)
                .createdAt(LocalDateTime.now())
                .groupId(groupId)
                .createdBy(adminId)
                .memberIds(memberIds)
                .build();

        return challengeRepository.save(challenge);
    }
}
