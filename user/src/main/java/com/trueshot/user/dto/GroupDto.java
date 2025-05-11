package com.trueshot.user.dto;

import com.trueshot.user.model.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {
    private String id;
    private String name;
    private UserDto admin;
    private Set<UserDto> members;

    public static GroupDto fromGroup(Group group) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setAdmin(UserDto.fromUser(group.getAdmin()));
        dto.setMembers(group.getUserList().stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toSet()));
        return dto;
    }
} 