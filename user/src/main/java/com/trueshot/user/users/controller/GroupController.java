package com.trueshot.user.users.controller;

import com.trueshot.user.users.model.Group;
import com.trueshot.user.users.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestParam String name, Authentication authentication) {
        String adminUsername = authentication.getName();
        Group group = groupService.createGroup(name, adminUsername);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<Group> joinGroup(@PathVariable UUID groupId, Authentication authentication) {
        String username = authentication.getName();
        Group group = groupService.joinGroup(groupId, username);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Group> leaveGroup(@PathVariable UUID groupId, Authentication authentication) {
        String username = authentication.getName();
        Group group = groupService.leaveGroup(groupId, username);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable UUID groupId, Authentication authentication) {
        String username = authentication.getName();
        groupService.deleteGroup(groupId, username);
        return ResponseEntity.ok("Group deleted successfully");
    }
}
