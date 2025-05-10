package com.trueshot.user.users.dto;

import jakarta.persistence.Id;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.trueshot.user.users.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class UserDto {
    @Id
    private String name;
    private String password;

    public static UserDto fromUser(User user) {
        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setPassword(user.getPassword());
        return dto;
    }
}
