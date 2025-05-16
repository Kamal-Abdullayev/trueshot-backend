package com.trueshot.challange.service;

import com.trueshot.challange.constant.ConsumerConstant;
import com.trueshot.challange.constant.KafkaConfigConstant;
import com.trueshot.challange.dto.*;
import com.trueshot.challange.entity.Challenge;
import com.trueshot.challange.jwt.JwtService;
import com.trueshot.challange.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeService {
    private final JwtService jwtService;
    private final ChallengeRepository challengeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaConfigConstant configConstant;
    private final RestTemplate restTemplate;

    public ChallengeResponseDto createChallenge(CreateChallengeRequestDto createChallengeRequestDto, String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        log.debug("Token: {}", token);
        String username = jwtService.extractUsername(token);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endTime = LocalDateTime.parse(createChallengeRequestDto.getEndTime(), formatter);

        
        log.info("Challenge creation details:");
        log.info("Input end time: {}", createChallengeRequestDto.getEndTime());
        log.info("Final zoned time: {}", endTime);
        log.info("Current time: {}", ZonedDateTime.now(ZoneId.of("Europe/Istanbul")));

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

        // 1. Fetch the entity
        Challenge challenge = challengeRepository
                .findChallengeById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        // 2. Check “ended” state
        LocalDateTime now = LocalDateTime.now();
        if (challenge.getEndTime().isBefore(now)) {
            log.info("Challenge has ended. End time: {}, Current time: {}",
                    challenge.getEndTime(), now);
            throw new RuntimeException("Challenge has ended");
        }

        // 3. Convert to DTO and return
        return ChallengeResponseDto.convert(challenge);
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


    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkEndedChallengesAndAssignRewards() {
        LocalDateTime now = LocalDateTime.now();
        List<Challenge> ended = challengeRepository
                .findByEndTimeBeforeAndRewardAssignedFalse(now);

        for (Challenge challenge : ended) {
            try {
                // 1) Fetch all posts for this challenge
                PostResponseDto[] posts = restTemplate.getForObject(
                        "http://post/api/v1/post/challenge/" + challenge.getId(),
                        PostResponseDto[].class
                );

                if (posts == null || posts.length == 0) {
                    log.info("No posts for challenge {}", challenge.getId());
                } else {
                    // 2) Find the post with the highest upVotes
                    PostResponseDto top = Arrays.stream(posts)
                            .max(Comparator.comparingInt(p -> p.getVote().getUpVotes()))
                            .orElse(null);

                    if (top != null && top.getVote().getUpVotes() > 0) {
                        String winnerId = top.getUserId();
                        log.info("Assigning reward '{}' to user '{}' for challenge '{}'",
                                challenge.getChallengeRewardTag(),
                                winnerId,
                                challenge.getId());

                        // 3) Send reward to user service
                        restTemplate.postForObject(
                                "http://user/api/v1/auth/" + winnerId + "/rewards",
                                challenge.getChallengeRewardTag(),
                                Void.class
                        );
                    } else {
                        log.info("No upvoted posts for challenge {}", challenge.getId());
                    }
                }

                // 4) Mark this challenge so it won't be picked up again
                challenge.setRewardAssigned(true);
                challengeRepository.save(challenge);
                log.info("Marked challenge {} as rewardAssigned", challenge.getId());

            } catch (Exception e) {
                log.error("Error processing challenge {}: {}",
                        challenge.getId(), e.getMessage(), e);
                // continue to next challenge; leave rewardAssigned=false so we retry later
            }
        }
    }

}
