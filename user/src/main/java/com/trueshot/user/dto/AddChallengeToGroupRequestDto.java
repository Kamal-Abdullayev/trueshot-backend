package com.trueshot.user.dto;

import lombok.Data;

@Data
public class AddChallengeToGroupRequestDto {
    private String challengeId;
    private String groupId;

}
