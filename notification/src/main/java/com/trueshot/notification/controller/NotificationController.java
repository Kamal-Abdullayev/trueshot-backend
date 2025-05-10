package com.trueshot.notification.controller;

import com.trueshot.notification.entity.Notification;
import com.trueshot.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestParam String userId,
            @RequestParam String message) {
        return ResponseEntity.ok(service.createNotification(userId, message));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(service.getNotifications(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        service.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/user/{userId}/read")
    public ResponseEntity<String> markAllAsRead(@PathVariable String userId) {
        int updatedCount = service.markAllAsRead(userId);
        return ResponseEntity.ok(updatedCount + " notifications marked as read.");
    }

}
