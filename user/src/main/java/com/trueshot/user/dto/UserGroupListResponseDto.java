package com.trueshot.user.dto;

import com.trueshot.user.model.Group;
import com.trueshot.user.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class UserGroupListResponseDto {
    List<Group> groupIds;


    public static UserGroupListResponseDto convert(User user) {
        return UserGroupListResponseDto.builder()
                .groupIds(user.getGroups())
                .build();
    }
}
