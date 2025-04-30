package com.trueshot.user.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.trueshot.user.users.model.User;
import com.trueshot.user.users.repository.UserRepository;


@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
       Optional<User> user = userRepository.findByName(username);
       user
       .orElseThrow(() -> new UsernameNotFoundException(username + "not found"));
               return user.map(MyUserDetails::new).get();
   }
}
