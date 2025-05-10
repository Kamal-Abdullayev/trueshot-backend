package com.trueshot.user.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@ToString(exclude = { "following", "followers" })
@EqualsAndHashCode(exclude = { "following", "followers" })
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String id;

    @Column(unique = true)
    private String name;

    private float point;

    private String password;

    private String roles;
    private Reward groupAccessRole;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_followers", joinColumns = @JoinColumn(name = "follower_id"), inverseJoinColumns = @JoinColumn(name = "following_id"))
    @JsonIgnore
    private Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> followers = new HashSet<>();

    @ElementCollection
    private List<String> challengeIds;

    @ManyToMany(mappedBy = "userList", fetch = FetchType.LAZY)
    private List<Group> groups;


}
