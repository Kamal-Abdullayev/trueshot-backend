package com.trueshot.user.service;

import com.trueshot.user.jwt.JwtService;
import com.trueshot.user.dto.UserGroupListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.time.LocalDateTime;

import com.trueshot.user.model.User;
import com.trueshot.user.repository.UserRepository;
import com.trueshot.user.model.Reward;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public User addUser(User user) {
        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new IllegalArgumentException("User with name '" + user.getName() + "' already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllUsersExceptCurrent(String currentUsername) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getName().equals(currentUsername))
                .collect(Collectors.toList());
    }

    public UserGroupListResponseDto getUserGroups(String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        log.debug("Token: {}", token);
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );
        log.info("Get all groups which user is a member of: {}", user.getName());
        return UserGroupListResponseDto.convert(user);
    }

    public List<Reward> getUserRewards(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return user.getRewards();
    }

    public void addRewardToUser(String userId, Reward reward) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        if (user.getRewards() == null) {
            user.setRewards(new ArrayList<>());
        }
        user.getRewards().add(reward);
        userRepository.save(user);
    }

}
