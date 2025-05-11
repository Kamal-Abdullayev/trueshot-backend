package com.trueshot.challange.controller;

import com.trueshot.challange.dto.ChallengeListResponseDto;
import com.trueshot.challange.dto.ChallengeResponseDto;
import com.trueshot.challange.dto.CreateChallengeRequestDto;
import com.trueshot.challange.entity.Challenge;
import com.trueshot.challange.service.ChallengeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/api/v1/challenge")
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<ChallengeResponseDto> createChallenge(@RequestBody CreateChallengeRequestDto createChallengeRequestDto,
                                                                @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(challengeService.createChallenge(createChallengeRequestDto, authHeader));
    }


    @GetMapping("/all")
    public ResponseEntity<List<ChallengeListResponseDto>> getAllChallenges(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                           @RequestParam(name = "size", defaultValue = "5") int size) {

        return ResponseEntity.ok(challengeService.retrieveAllChallengeList(PageRequest.of(page, size)));

    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeResponseDto> getChallengeById(@PathVariable("challengeId") String challengeId) {
        return ResponseEntity.ok(challengeService.getChallengeById(challengeId));
    }



}
