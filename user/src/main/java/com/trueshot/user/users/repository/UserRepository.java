package com.trueshot.user.users.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


import  com.trueshot.user.users.model.User;

public interface UserRepository extends JpaRepository<User, UUID>{
    Optional <User> findByName(String username);
}
