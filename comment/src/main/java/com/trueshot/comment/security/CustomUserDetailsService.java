package com.trueshot.comment.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {

        // Mock example: Use hardcoded UUID (or fetch from DB/user service)
        UUID userId = UUID.nameUUIDFromBytes(username.getBytes());

        return new UserPrincipal(userId, username);
    }
}
