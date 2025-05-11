package com.trueshot.user.dto;

import com.trueshot.user.model.Reward;
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

}
