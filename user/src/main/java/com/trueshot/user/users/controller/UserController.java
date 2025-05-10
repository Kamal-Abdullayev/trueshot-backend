package com.trueshot.user.users.controller;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import com.trueshot.user.jwt.JwtService;
import com.trueshot.user.users.dto.UserDto;
import com.trueshot.user.users.model.User;
import com.trueshot.user.users.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody UserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getName(), userDto.getPassword()));

        if (authentication.isAuthenticated()) {
            User user = userService.getUserByUsername(userDto.getName());
            String token = jwtService.generateToken(user.getId().toString(), userDto.getName()); // Include userId in token
            log.info("User authenticated successfully: {}", userDto.getName());
            log.info("Generated Token: {}", token);
            return token;
        } else {
            log.error("Failed to authenticate user: {}", userDto.getName());
            throw new UsernameNotFoundException("The user cannot be authenticated");
        }
    }

    @PostMapping("/signup")
    public User signupUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping("/user/id-by-username")
    public String getUserIdByUsername(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        return user.getId().toString();
    }

    @GetMapping("/user/ids")
    public List<UUID> getAllUserIds() {
        return userService.getAllUsers()
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @GetMapping("/users")
    public List<User> getAllUsers(Authentication authentication) {
        return userService.getAllUsersExceptCurrent(authentication.getName());
    }
}
