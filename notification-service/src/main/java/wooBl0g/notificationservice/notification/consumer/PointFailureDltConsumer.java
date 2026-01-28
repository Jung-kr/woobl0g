package wooBl0g.notificationservice.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import wooBl0g.notificationservice.notification.event.*;
import wooBl0g.notificationservice.notification.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointFailureDltConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user.signed-up.dlt", groupId = "notification-service")
    public void consumeUserSignedUp(String message) {
        log.warn("회원가입 포인트 실패 알림 - DLT 수신: message={}", message);
        
        try {
            UserSignedUpEvent event = UserSignedUpEvent.fromJson(message);
            notificationService.sendNotification(event.getUserId(), event.getActionType());
            
            log.info("회원가입 포인트 실패 알림 전송 완료: userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("회원가입 Slack 알림 전송 실패: message={}", message, e);
        }
    }

    @KafkaListener(topics = "board.created.dlt", groupId = "notification-service")
    public void consumeBoardCreated(String message) {
        log.warn("게시글 생성 포인트 실패 알림 - DLT 수신: message={}", message);
        
        try {
            BoardCreatedEvent event = BoardCreatedEvent.fromJson(message);
            notificationService.sendNotification(event.getUserId(), event.getActionType());
            
            log.info("게시글 생성 포인트 실패 알림 전송 완료: userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("게시글 생성 Slack 알림 전송 실패: message={}", message, e);
        }
    }

    @KafkaListener(topics = "comment.created.dlt", groupId = "notification-service")
    public void consumeCommentCreated(String message) {
        log.warn("댓글 생성 포인트 실패 알림 - DLT 수신: message={}", message);
        
        try {
            CommentCreatedEvent event = CommentCreatedEvent.fromJson(message);
            notificationService.sendNotification(event.getUserId(), event.getActionType());
            
            log.info("댓글 생성 포인트 실패 알림 전송 완료: userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("댓글 생성 Slack 알림 전송 실패: message={}", message, e);
        }
    }

    @KafkaListener(topics = "bet.cancelled.dlt", groupId = "notification-service")
    public void consumeBetCancelled(String message) {
        log.warn("베팅 취소 포인트 실패 알림 - DLT 수신: message={}", message);

        try {
            BetCancelledEvent event = BetCancelledEvent.fromJson(message);
            notificationService.sendNotification(event.getUserId(), event.getActionType());

            log.info("베팅 취소 포인트 실패 알림 전송 완료: userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("베팅 취소 Slack 알림 전송 실패: message={}", message, e);
        }
    }

    @KafkaListener(topics = "bet.settled.dlt", groupId = "notification-service")
    public void consumeBetSettled(String message) {
        log.warn("베팅 정산 포인트 실패 알림 - DLT 수신: message={}", message);

        try {
            BetSettledEvent event = BetSettledEvent.fromJson(message);
            notificationService.sendNotification(event.getUserId(), event.getActionType());

            log.info("베팅 정산 포인트 실패 알림 전송 완료: userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("베팅 정산 Slack 알림 전송 실패: message={}", message, e);
        }
    }
}
