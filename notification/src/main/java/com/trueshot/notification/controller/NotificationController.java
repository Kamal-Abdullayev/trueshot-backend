package com.trueshot.notification.controller;

import com.trueshot.notification.dto.NotificationDTO;
import com.trueshot.notification.entity.Notification;
import com.trueshot.notification.jwt.JwtService;
import com.trueshot.notification.scheduler.DailyChallengeScheduler;
import com.trueshot.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;
    private final DailyChallengeScheduler dailyChallengeScheduler;
    private final JwtService jwtService;
    private final WebClient webClient;

    @GetMapping("/test-daily-challenge")
    public ResponseEntity<Void> testDailyChallenge() {
        dailyChallengeScheduler.sendChallengeNotificationToAllUsers();
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> createNotification(@RequestBody NotificationDTO dto) {
        service.sendNotification(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String username = jwtService.extractUsername(token);
        
        // Get user ID from user service
        String userId = webClient.get()
                .uri("http://localhost:8087/api/v1/auth/user/id-by-username?username=" + username)
                .retrieve()
                .bodyToMono(String.class)
                .block();
                
        return ResponseEntity.ok(service.getNotificationsForUser(userId));
    }
}
