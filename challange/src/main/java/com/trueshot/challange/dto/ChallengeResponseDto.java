package com.trueshot.challange.dto;

import com.trueshot.challange.entity.Challenge;
import com.trueshot.challange.entity.Reward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeResponseDto {
    private String title;
    private String content;
    private String groupId;
    private String createdBy;
    private float point;
    private Reward challengeRewardTag;
    private LocalDateTime endTime;

    public static ChallengeResponseDto convert(Challenge challenge) {
        return ChallengeResponseDto.builder()
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .groupId(challenge.getGroupId())
                .createdBy(challenge.getCreatedBy())
                .point(challenge.getPoint())
                .challengeRewardTag(challenge.getChallengeRewardTag())
                .endTime(challenge.getEndTime())
                .build();
    }

}
