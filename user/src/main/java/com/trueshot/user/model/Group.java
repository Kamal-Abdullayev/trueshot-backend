package com.trueshot.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "user_groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @ElementCollection
    private List<String> challengeIds;

    @ElementCollection(targetClass = Reward.class)
    @Enumerated(EnumType.STRING)
    private List<Reward> accessRoles;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @ManyToMany
    @JoinTable(name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> userList;
}
