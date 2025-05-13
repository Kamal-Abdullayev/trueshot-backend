package com.trueshot.user.service;

import com.trueshot.user.constant.ConsumerConstant;
import com.trueshot.user.dto.ChallengeRegisterGroupDto;
import com.trueshot.user.dto.ChallengeResponseDto;
import com.trueshot.user.dto.PostResponseDto;
import com.trueshot.user.jwt.JwtService;
import com.trueshot.user.dto.AddChallengeToGroupRequestDto;
import com.trueshot.user.model.Group;
import com.trueshot.user.model.User;
import com.trueshot.user.repository.GroupRepository;
import com.trueshot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ResourceLoader;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final WebClient webClient;
    private final ResourceLoader resourceLoader;


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

    public Group getGroupById(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    @Transactional
    @KafkaListener(topics = {ConsumerConstant.TOPIC_NAME}, groupId = ConsumerConstant.GROUP_ID)
    public void addChallengeToGroup(ChallengeRegisterGroupDto challenge) {
        log.info("Received challenge: {}", challenge);

        Group group = groupRepository.findById(challenge.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group.getChallengeIds() == null) {
            group.setChallengeIds(new ArrayList<>());
        }
        group.getChallengeIds().add(challenge.getChallengeId());
        groupRepository.save(group);
        log.info("Challenge \"{}\" added to group: {}", challenge.getTitle(), group.getName());
    }

    public void addChallengeToGroup(AddChallengeToGroupRequestDto requestDto) {
        Group group = groupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group.getChallengeIds() == null) {
            group.setChallengeIds(new ArrayList<>());
        }

        group.getChallengeIds().add(requestDto.getChallengeId());
        groupRepository.save(group);
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

    public List<Group> getAllGroupsExceptUserJoined(String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String username = jwtService.extractUsername(token);
        log.info("Get all groups except user joined: {}", username);

        User user = userRepository.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );

        List<Group> groups =  groupRepository.findAll().stream()
                .filter(group -> !group.getUserList().contains(user))
                .toList();
        log.info("The groups that the user is not a member of: {}", groups);
        return groups;
    }

    public List<PostResponseDto> getGroupChallengePostsByGroupId(String groupId, String authHeader) {

        String lastChallengeId = getGroupLastChallengeId(groupId);
        log.info("Last challenge ID: {}", lastChallengeId);

        List<PostResponseDto> postResponseDtoList = webClient.get()
                .uri("/api/v1/post/challenge/" + lastChallengeId)
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PostResponseDto>>() {})
                .block();

        log.info("The post related to the challenge ID: {} retrieved", postResponseDtoList);

        return postResponseDtoList;
    }

    public ChallengeResponseDto getGroupLastChallenge(String groupId, String authHeader) {
        String challengeId = getGroupLastChallengeId(groupId);

        ChallengeResponseDto postResponseDto = webClient.get()
                .uri("/api/v1/challenge/" + challengeId)
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(ChallengeResponseDto.class)
                .block();

        if (postResponseDto == null) {
            throw new RuntimeException("Challenge not found");
        }

        log.info("List of the posts for challenge Id: {} retrieved", challengeId);
        postResponseDto.setChallengeId(challengeId);
        return postResponseDto;
    }


    private String getGroupLastChallengeId(String groupId) {
        Group group = getGroupObjectByIdO(groupId);

        if (group.getChallengeIds() == null || group.getChallengeIds().isEmpty()) {
            throw new RuntimeException("No challenges found in the group");
        }
        String challengeId = group.getChallengeIds().getLast();
        log.info("The last challenge ID: {} of {} group", challengeId, group.getName());
        return challengeId;
    }
    private Group getGroupObjectByIdO(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }
}
