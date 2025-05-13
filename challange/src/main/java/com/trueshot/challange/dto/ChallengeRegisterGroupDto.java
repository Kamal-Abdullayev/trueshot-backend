package com.trueshot.challange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChallengeRegisterGroupDto {
    private String challengeId;
    private String title;
    private String groupId;
}
