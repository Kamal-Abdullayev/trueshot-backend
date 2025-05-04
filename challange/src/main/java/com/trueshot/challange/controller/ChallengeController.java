package com.trueshot.challange.controller;

import com.trueshot.challange.entity.Challenge;
import com.trueshot.challange.service.ChallengeService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @PostMapping("/create")
    public ResponseEntity<Challenge> createChallenge(@RequestBody CreateChallengeRequest request) {
        Challenge challenge = challengeService.createChallenge(
                request.getContent(),
                request.getGroupId(),
                request.getAdminId(),
                request.getMemberIds()
        );
        return ResponseEntity.ok(challenge);
    }

    @Data
    public static class CreateChallengeRequest {
        private String content;
        private UUID groupId;
        private UUID adminId;
        private Set<UUID> memberIds;
    }
}
