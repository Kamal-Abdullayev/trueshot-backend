package com.trueshot.challange.dto;

import com.trueshot.challange.entity.Challenge;
import com.trueshot.challange.entity.Reward;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ChallengeListResponseDto {
    private String title;
    private String content;
    private String groupId;
    private String createdBy;
    private float point;
    private Reward challengeRewardTag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime endTime;
    private Set<String> memberIds;

    public static ChallengeListResponseDto convert(Challenge challenge) {
        return ChallengeListResponseDto.builder()
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .groupId(challenge.getGroupId())
                .createdBy(challenge.getCreatedBy())
                .point(challenge.getPoint())
                .challengeRewardTag(challenge.getChallengeRewardTag())
                .createdAt(challenge.getCreatedAt())
                .updatedAt(challenge.getUpdatedAt())
                .endTime(challenge.getEndTime())
                .memberIds(challenge.getMemberIds())
                .build();
    }

}
