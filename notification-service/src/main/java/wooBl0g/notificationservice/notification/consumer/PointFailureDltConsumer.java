package wooBl0g.notificationservice.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import wooBl0g.notificationservice.notification.event.BoardCreatedEvent;
import wooBl0g.notificationservice.notification.event.CommentCreatedEvent;
import wooBl0g.notificationservice.notification.event.UserSignedUpEvent;
import wooBl0g.notificationservice.notification.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointFailureDltConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user.signed-up.dlt", groupId = "notification-service")
    public void consumeUserSignedUp(String message) {
        try {
            UserSignedUpEvent event = UserSignedUpEvent.fromJson(message);
            notificationService.sendNotification(event.getUserId(), event.getActionType());
        } catch (Exception e) {
            log.error("Slack 알림 전송 실패: {}", message, e);
        }
    }

    @KafkaListener(topics = "board.created.dlt", groupId = "notification-service")
    public void consumeBoardCreated(String message) {
        try {
            BoardCreatedEvent event = BoardCreatedEvent.fromJson(message);
            notificationService.sendNotification(event.getUserId(), event.getActionType());
        } catch (Exception e) {
            log.error("Slack 알림 전송 실패: {}", message, e);
        }
    }

    @KafkaListener(topics = "comment.created.dlt", groupId = "notification-service")
    public void consumeCommentCreated(String message) {
        try {
            CommentCreatedEvent event = CommentCreatedEvent.fromJson(message);
            notificationService.sendNotification(event.getUserId(), event.getActionType());
        } catch (Exception e) {
            log.error("Slack 알림 전송 실패: {}", message, e);
        }
    }
}
