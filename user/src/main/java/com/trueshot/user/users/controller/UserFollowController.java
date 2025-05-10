package com.trueshot.user.users.controller;

import com.trueshot.user.jwt.JwtService;
import com.trueshot.user.users.dto.UserResponseDto;
import com.trueshot.user.users.dto.UserSuggestionDto;
import com.trueshot.user.users.model.User;
import com.trueshot.user.users.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
public class UserFollowController {
    @Autowired
    private JwtService jwtService;

    private final UserRepository userRepository;

    @Operation(summary = "Follow a user")
    @PostMapping("/follow/{followingId}")
    @Transactional
    public ResponseEntity<String> followUser(Authentication authentication, @PathVariable String followingId) {
        User follower = userRepository.findByName(authentication.getName()).orElseThrow();
        User following = userRepository.findById(followingId).orElseThrow();

        if (follower.getFollowing().stream().anyMatch(u -> u.getId().equals(following.getId()))) {
            return ResponseEntity.badRequest().body("Already following this user");
        }

        follower.getFollowing().add(following);
        following.getFollowers().add(follower);

        userRepository.save(follower);
        userRepository.save(following);

        return ResponseEntity.ok("Followed successfully");
    }

    @Operation(summary = "Unfollow a user")
    @PostMapping("/unfollow/{followingId}")
    @Transactional
    public ResponseEntity<String> unfollowUser(Authentication authentication, @PathVariable String followingId) {
        User follower = userRepository.findByName(authentication.getName()).orElseThrow();
        User following = userRepository.findById(followingId).orElseThrow();

        boolean removed = follower.getFollowing().removeIf(u -> u.getId().equals(following.getId()));

        if (!removed) {
            return ResponseEntity.badRequest().body("You are not following this user");
        }

        following.getFollowers().remove(follower);

        userRepository.save(follower);
        userRepository.save(following);

        return ResponseEntity.ok("Unfollowed successfully");
    }

    @Operation(summary = "List following users")
    @Transactional
    @GetMapping("/following")
    public List<UserResponseDto> getFollowing(Authentication authentication) {
        User user = userRepository.findByName(authentication.getName()).orElseThrow();
        return user.getFollowing().stream()
                .map(u -> new UserResponseDto(u.getId(), u.getName()))
                .collect(Collectors.toList());
    }

    @Operation(summary = "List followers")
    @Transactional
    @GetMapping("/followers")
    public List<UserResponseDto> getFollowers(Authentication authentication) {
        User user = userRepository.findByName(authentication.getName()).orElseThrow();
        return user.getFollowers().stream()
                .map(u -> new UserResponseDto(u.getId(), u.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/suggestions")
    public List<UserSuggestionDto> getSuggestions(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String currentUsername = jwtService.extractUsername(token);

        User currentUser = userRepository.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .filter(u -> !currentUser.getFollowing().contains(u))
                .map(u -> new UserSuggestionDto(u.getId(), u.getName()))
                .collect(Collectors.toList());

    }

}
