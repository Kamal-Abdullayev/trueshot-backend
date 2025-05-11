package com.trueshot.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.trueshot.user.model.User;

public interface UserRepository extends JpaRepository<User, String>{
    Optional <User> findByName(String username);
}
