package com.trueshot.feed.dto;

import lombok.Data;

import java.util.Set;

@Data
public class VoteResponseDto {
    private String id;

    private int upVotes;
    private int downVotes;

    private Set<String> userIdsUpVoted;
    private Set<String> userIdsDownVoted;

}
