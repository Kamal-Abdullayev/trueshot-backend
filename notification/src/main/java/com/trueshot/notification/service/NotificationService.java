package com.trueshot.notification.service;

import com.trueshot.notification.dto.NotificationDTO;
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

    public void sendNotification(NotificationDTO dto) {
        Notification notification = Notification.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();
        repository.save(notification);
    }

    public List<Notification> getNotificationsForUser(String userId) {
        return repository.findByUserIdOrderByTimestampDesc(userId);
    }
}
