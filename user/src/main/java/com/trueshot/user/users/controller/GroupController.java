package com.trueshot.user.users.controller;

import com.trueshot.user.users.model.Group;
import com.trueshot.user.users.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
