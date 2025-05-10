package com.trueshot.notification.service;

import com.trueshot.notification.entity.Notification;
import com.trueshot.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public Notification createNotification(String userId, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();
        return repository.save(notification);
    }

    public List<Notification> getNotifications(String userId) {
        return repository.findByUserIdOrderByTimestampDesc(userId);
    }

    public Notification markAsRead(Long id) {
        return repository.findById(id)
                .map(notification -> {
                    notification.setRead(true);
                    return repository.save(notification);
                }).orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    public void deleteNotification(Long id) {
        repository.deleteById(id);
    }

    public int markAllAsRead(String userId) {
        return repository.markAllAsReadByUserId(userId);
    }

}
