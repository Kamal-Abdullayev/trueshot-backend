package com.trueshot.challange.dto;

import com.trueshot.challange.entity.Challenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeResponseDto {
    private String content;
    private String groupId;
    private String createdBy;
    private Set<String> memberIds;

    public static ChallengeResponseDto convert(Challenge challenge) {
        return ChallengeResponseDto.builder()
                .content(challenge.getContent())
                .groupId(challenge.getGroupId())
                .createdBy(challenge.getCreatedBy())
                .memberIds(challenge.getMemberIds())
                .build();
    }

}
