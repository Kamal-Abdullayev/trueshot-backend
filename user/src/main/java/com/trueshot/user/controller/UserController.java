package com.trueshot.user.controller;

import com.trueshot.user.dto.UserGroupListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import com.trueshot.user.jwt.JwtService;
import com.trueshot.user.dto.UserDto;
import com.trueshot.user.model.User;
import com.trueshot.user.service.UserService;
import com.trueshot.user.model.Reward;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
    public List<String> getAllUserIds() {
        return userService.getAllUsers()
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @GetMapping("/users")
    public List<User> getAllUsers(Authentication authentication) {
        return userService.getAllUsersExceptCurrent(authentication.getName());
    }

    @GetMapping("/user-groups")
    public ResponseEntity<UserGroupListResponseDto> getUserGroups(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(userService.getUserGroups(authHeader));
    }

    @GetMapping("/{userId}/rewards")
    public ResponseEntity<List<Reward>> getUserRewards(@PathVariable String userId) {
        List<Reward> rewards = userService.getUserRewards(userId);
        return ResponseEntity.ok(rewards);
    }

    @PostMapping("/{userId}/rewards")
    public ResponseEntity<Void> addRewardToUser(@PathVariable String userId, @RequestBody Reward reward) {
        userService.addRewardToUser(userId, reward);
        return ResponseEntity.ok().build();
    }

}
