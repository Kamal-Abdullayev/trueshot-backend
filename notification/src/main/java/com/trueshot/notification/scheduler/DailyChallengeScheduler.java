package com.trueshot.notification.scheduler;

import com.trueshot.notification.client.UserClient;
import com.trueshot.notification.dto.NotificationDTO;
import com.trueshot.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class DailyChallengeScheduler {

    private final NotificationService notificationService;
    private final UserClient userClient;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Random random = new Random();

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyChallenge() {
        LocalTime randomTime = getRandomTimeBetween(9, 21);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetDateTime = now.with(randomTime);

        if (now.isAfter(targetDateTime)) {
            targetDateTime = targetDateTime.plusDays(1);
        }

        long delay = Duration.between(now, targetDateTime).toMillis();
        executor.schedule(this::sendChallengeNotificationToAllUsers, delay, TimeUnit.MILLISECONDS);

        System.out.println("Scheduled challenge notification at " + targetDateTime);
    }

    private LocalTime getRandomTimeBetween(int startHour, int endHour) {
        int hour = startHour + random.nextInt(endHour - startHour + 1);
        int minute = random.nextInt(60);
        return LocalTime.of(hour, minute);
    }

    public void sendChallengeNotificationToAllUsers() {
        List<UUID> userIds = userClient.getAllUserIds();
        for (UUID userId : userIds) {
            NotificationDTO dto = NotificationDTO.builder()
                    .userId(userId)
                    .message("📸 It's time to post on Trueshot!")
                    .build();
            notificationService.sendNotification(dto);
        }
        System.out.println("Challenge notifications sent to " + userIds.size() + " users.");
    }
}
