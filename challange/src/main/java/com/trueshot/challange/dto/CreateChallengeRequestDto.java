package com.trueshot.challange.dto;

import com.trueshot.challange.entity.Reward;
import lombok.Data;

@Data
public class CreateChallengeRequestDto {
    private String title;
    private String content;
    private String groupId;
    private float point;
    private Reward challengeRewardTag;
    private String endTime;

}
