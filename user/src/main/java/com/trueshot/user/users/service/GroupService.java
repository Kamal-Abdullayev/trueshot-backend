package com.trueshot.user.users.service;

import com.trueshot.user.users.model.Group;
import com.trueshot.user.users.model.User;
import com.trueshot.user.users.repository.GroupRepository;
import com.trueshot.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public Group createGroup(String name, String adminUsername) {
        User admin = userRepository.findByName(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        Group group = Group.builder()
                .name(name)
                .admin(admin)
                .userList(new ArrayList<>())
                .build();

        return groupRepository.save(group);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Transactional
    public Group joinGroup(String groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (group.getUserList().contains(user)) {
            throw new RuntimeException("User is already a member of this group");
        }

        group.getUserList().add(user);
        return groupRepository.save(group);
    }

    @Transactional
    public Group leaveGroup(String groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!group.getUserList().contains(user)) {
            throw new RuntimeException("User is not a member of this group");
        }

        if (group.getAdmin().equals(user)) {
            throw new RuntimeException("Admin cannot leave the group. Transfer admin rights first.");
        }

        group.getUserList().remove(user);
        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(String groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!group.getAdmin().equals(user)) {
            throw new RuntimeException("Only the admin can delete the group");
        }

        groupRepository.delete(group);
    }
}
