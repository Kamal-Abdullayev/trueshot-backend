package com.trueshot.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "votes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private int upVotes;
    private int downVotes;

    @ElementCollection
    private Set<String> userIdsUpVoted = new HashSet<>();

    @ElementCollection
    private Set<String> userIdsDownVoted = new HashSet<>();
}
