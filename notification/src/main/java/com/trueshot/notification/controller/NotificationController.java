package com.trueshot.notification.controller;

import com.trueshot.notification.dto.NotificationDTO;
import com.trueshot.notification.entity.Notification;
import com.trueshot.notification.scheduler.DailyChallengeScheduler;
import com.trueshot.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;
    private final DailyChallengeScheduler dailyChallengeScheduler;

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

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getNotificationsForUser(userId));
    }
}
