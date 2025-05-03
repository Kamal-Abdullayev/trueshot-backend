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
//@CrossOrigin(origins = "http://localhost:8080/") //@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // a new end-point that allows users to authenticate themselves and generate the jwt token
    //This endpoint will receive the userDto, authenticate her/him with existing users in the database, then if authenticated, it will create the jwt
    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody UserDto userDto) {
        
        // authenticationManager.authenticate attempts to authenticate the passed Authentication object, returning a fully populated Authentication object (including granted authorities) if successful.
        // UsernamePasswordAuthenticationToken can be used by the authenticationManager and we are passing the user name and password to it.
        // To use the authenticationManager, you need to define a Bean for it, check SecurityConfig.java, it is defined there.
        // Note that verifying the user is a required before generating the token, otherwise, we will be generating tokens for users that we cannot authenticate
        
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getName(), userDto.getPassword()));
       // If the user is authenticated we generate the token, otherwise, we throw an exception

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userDto.getName());
        } else {
            throw new UsernameNotFoundException("The user cannot be authenticated");
        }
    }


    // an end point for signing up new users
    @PostMapping("/signup")
    public User signupUser(@RequestBody User user){
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




}
