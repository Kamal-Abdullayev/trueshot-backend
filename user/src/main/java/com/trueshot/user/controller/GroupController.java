package com.trueshot.user.controller;

import com.trueshot.user.dto.AddChallengeToGroupRequestDto;
import com.trueshot.user.dto.ChallengeResponseDto;
import com.trueshot.user.dto.PostResponseDto;
import com.trueshot.user.model.Group;
import com.trueshot.user.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestParam String name,
                                             @RequestParam(required = false, defaultValue = "false") boolean exclusive,
                                             @RequestParam(required = false) List<String> allowedUsernames,
                                             Authentication authentication) {
        String adminUsername = authentication.getName();
        Group group = groupService.createGroup(name, adminUsername, exclusive, allowedUsernames);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Group>> getAllGroups(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(groupService.getAllGroupsVisibleToUser(username));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable("groupId") String groupId) {
        Group group = groupService.getGroupById(groupId);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/add-challenge")
    public ResponseEntity<HttpStatus> addChallengeToGroup(@RequestBody AddChallengeToGroupRequestDto requestDto) {
        groupService.addChallengeToGroup(requestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<Group> joinGroup(@PathVariable String groupId, Authentication authentication) {
        String username = authentication.getName();
        Group group = groupService.joinGroup(groupId, username);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Group> leaveGroup(@PathVariable String groupId, Authentication authentication) {
        String username = authentication.getName();
        Group group = groupService.leaveGroup(groupId, username);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable String groupId, Authentication authentication) {
        String username = authentication.getName();
        groupService.deleteGroup(groupId, username);
        return ResponseEntity.ok("Group deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(groupService.getAllGroupsExceptUserJoined(authHeader));
    }

    @GetMapping("/last-challenge/{groupId}")
    public ResponseEntity<ChallengeResponseDto> getLastChallenge(@PathVariable("groupId") String groupId,
                                                                       @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(groupService.getGroupLastChallenge(groupId, authHeader));
    }

    @GetMapping("/posts/{groupId}")
    public ResponseEntity<List<PostResponseDto>> getGroupPosts(@PathVariable("groupId") String groupId,
                                                               @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(groupService.getGroupChallengePostsByGroupId(groupId, authHeader));
    }

}
