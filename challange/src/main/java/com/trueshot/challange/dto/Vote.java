package com.trueshot.challange.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {


    private String id;

    private int upVotes;
    private int downVotes;


    private Set<String> userIdsUpVoted;


    private Set<String> userIdsDownVoted;
}
