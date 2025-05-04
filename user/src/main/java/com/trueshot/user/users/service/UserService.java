package com.trueshot.user.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.List;



import com.trueshot.user.users.model.User;
import com.trueshot.user.users.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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




}
