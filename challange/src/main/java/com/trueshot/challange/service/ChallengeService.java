package com.trueshot.challange.service;

import com.trueshot.challange.constant.ConsumerConstant;
import com.trueshot.challange.constant.KafkaConfigConstant;
import com.trueshot.challange.dto.*;
import com.trueshot.challange.entity.Challenge;
import com.trueshot.challange.jwt.JwtService;
import com.trueshot.challange.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeService {
    private final JwtService jwtService;
    private final ChallengeRepository challengeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaConfigConstant configConstant;


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
        ChallengeRegisterGroupDto challengeRegisterGroupDto = ChallengeRegisterGroupDto.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .groupId(challenge.getGroupId())
                .build();
        kafkaTemplate.send(configConstant.getPostPublishTopic(), challengeRegisterGroupDto);
        log.info("Challenge published to Kafka topic: {}, data: {}", configConstant.getPostPublishTopic(), challengeRegisterGroupDto);

        return ChallengeResponseDto.convert(challenge);
    }

    public List<ChallengeListResponseDto> retrieveAllChallengeList(Pageable pageable) {
        log.info("Retrieving all challenges from database");
        return challengeRepository.findAll(pageable).stream()
                .map(ChallengeListResponseDto::convert)
                .toList();
    }

    public ChallengeResponseDto getChallengeById(String challengeId) {
        log.info("Retrieving challenge by ID: {}", challengeId);
        return ChallengeResponseDto.convert(challengeRepository.findChallengeById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found")));
    }

    @Transactional
    @KafkaListener(topics = {ConsumerConstant.TOPIC_NAME}, groupId = ConsumerConstant.GROUP_ID)
    public void savePostIdToChallenge(PostChallengeSaveDto postChallengeSaveDto) {
        log.info("Received message from Kafka topic: {}, data: {}", ConsumerConstant.TOPIC_NAME, postChallengeSaveDto);

        if (!postChallengeSaveDto.getChallengeId().equals("0")) {
            Challenge challenge = challengeRepository.findById(postChallengeSaveDto.getChallengeId())
                    .orElseThrow(() -> new RuntimeException("Challenge not found"));

            Set<String> postIds = challenge.getPostIds();
            postIds.add(postChallengeSaveDto.getPostId());

            challenge.setPostIds(postIds);
            challengeRepository.save(challenge);
            log.info("Post ID saved to challenge: {}", postChallengeSaveDto.getPostId());
        }

    }


}
