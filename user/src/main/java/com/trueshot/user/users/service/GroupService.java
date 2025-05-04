package com.trueshot.user.users.service;

import com.trueshot.user.users.model.Group;
import com.trueshot.user.users.model.User;
import com.trueshot.user.users.repository.GroupRepository;
import com.trueshot.user.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public Group createGroup(String name, String adminUsername) {
        User admin = userRepository.findByName(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        // Only allow users where role is not ADMIN
        Set<User> members = userRepository.findAll().stream()
                .filter(u -> !u.getName().equals(adminUsername))
                .filter(u -> !"ADMIN".equalsIgnoreCase(u.getRoles()))
                .collect(Collectors.toSet());

        Group group = Group.builder()
                .name(name)
                .admin(admin)
                .members(members)
                .build();

        return groupRepository.save(group);
    }
}
