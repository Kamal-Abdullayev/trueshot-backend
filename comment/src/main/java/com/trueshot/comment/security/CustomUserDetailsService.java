package com.trueshot.comment.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {

        return new UserPrincipal(UUID.nameUUIDFromBytes(username.getBytes()).toString(), username);
    }
}
