package com.trueshot.user.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserSuggestionDto {
    private UUID id;
    private String name;
}
