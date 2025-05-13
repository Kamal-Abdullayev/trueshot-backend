package com.trueshot.notification.service;

import com.trueshot.notification.constant.ConsumerConstant;
import com.trueshot.notification.dto.NotificationDTO;
import com.trueshot.notification.entity.Notification;
import com.trueshot.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
        List<Notification> notifications = repository.findByUserIdOrderByTimestampDesc(userId);
        log.info("Notifications for user {}: {}", userId, notifications);
        return notifications;
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


    @KafkaListener(topics = {ConsumerConstant.TOPIC_NAME}, groupId = ConsumerConstant.GROUP_ID)
    public void getNotificationsFromKafka(NotificationDTO comment) {
        log.info("Received comment: {}", comment);
        Notification notification = Notification.builder()
                .userId(comment.getUserId())
                .message("Someone commented on your post!")
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();

        log.info("Comment saved");
        repository.save(notification);
    }

}
