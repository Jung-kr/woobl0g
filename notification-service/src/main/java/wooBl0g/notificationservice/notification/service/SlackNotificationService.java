package wooBl0g.notificationservice.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import wooBl0g.notificationservice.notification.client.SlackWebhookClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotificationService implements NotificationService{

    private final SlackWebhookClient slackWebhookClient;

    @Override
    public void sendNotification(Long userId, String actionType) {
        String message = formatMessage(userId, actionType);
        log.debug("message: {}", message);

        slackWebhookClient.sendMessage(message);
    }

    private String formatMessage(Long userId, String actionType) {
        return String.format(
                "⚠️ 포인트 적립 실패\n" +
                        "UserId: %s\n" +
                        "ActionType: %s\n" +
                        "실패시각: %s\n" +
                        "관리자 페이지: http://wooblog.com",
                userId,
                actionType,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}
