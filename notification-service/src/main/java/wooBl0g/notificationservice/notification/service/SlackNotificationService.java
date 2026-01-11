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
        log.info("Slack 알림 전송 시작: userId={}, actionType={}", userId, actionType);
        
        String message = formatMessage(userId, actionType);
        log.debug("전송 메시지: {}", message);

        slackWebhookClient.sendMessage(message);
        
        log.info("Slack 알림 전송 완료: userId={}", userId);
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
