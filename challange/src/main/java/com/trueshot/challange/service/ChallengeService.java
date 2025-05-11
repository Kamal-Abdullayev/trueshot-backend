package com.trueshot.challange.service;

import com.trueshot.challange.dto.ChallengeListResponseDto;
import com.trueshot.challange.dto.ChallengeResponseDto;
import com.trueshot.challange.dto.CreateChallengeRequestDto;
import com.trueshot.challange.entity.Challenge;
import com.trueshot.challange.jwt.JwtService;
import com.trueshot.challange.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeService {
    private final JwtService jwtService;
    private final ChallengeRepository challengeRepository;


    public ChallengeResponseDto createChallenge(CreateChallengeRequestDto createChallengeRequestDto, String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        log.debug("Token: {}", token);
        String username = jwtService.extractUsername(token);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endTime = LocalDateTime.parse(createChallengeRequestDto.getEndTime(), formatter);


        Challenge newChallenge = Challenge.builder()
                .title(createChallengeRequestDto.getTitle())
                .content(createChallengeRequestDto.getContent())
                .groupId(createChallengeRequestDto.getGroupId())
                .createdBy(username)
                .point(createChallengeRequestDto.getPoint())
                .challengeRewardTag(createChallengeRequestDto.getChallengeRewardTag())
                .endTime(endTime)
                .build();

        log.info("Challenge saved by user ID: {}", username);
        Challenge challenge = challengeRepository.save(newChallenge);
        return ChallengeResponseDto.convert(challenge);
    }

    public List<ChallengeListResponseDto> retrieveAllChallengeList(Pageable pageable) {
        log.info("Retrieving all challenges from database");
        return challengeRepository.findAll(pageable).stream()
                .map(ChallengeListResponseDto::convert)
                .toList();
    }



}
