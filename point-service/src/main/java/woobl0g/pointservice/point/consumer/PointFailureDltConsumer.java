package woobl0g.pointservice.point.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import woobl0g.pointservice.point.domain.PointFailureHistory;
import woobl0g.pointservice.point.event.BoardCreatedEvent;
import woobl0g.pointservice.point.event.CommentCreatedEvent;
import woobl0g.pointservice.point.event.UserSignedUpEvent;
import woobl0g.pointservice.point.repository.PointFailureHistoryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointFailureDltConsumer {

    private final PointFailureHistoryRepository pointFailureHistoryRepository;

    @KafkaListener(topics = "user.signed-up.dlt", groupId = "point-service")
    public void consumeUserSignedUp(String message) {
        try {
            UserSignedUpEvent event = UserSignedUpEvent.fromJson(message);
            pointFailureHistoryRepository.save(PointFailureHistory.create(event.getUserId(), event.getActionType()));
        } catch (Exception e) {
            log.error("[회원가입] 포인트 적립 실패 DLT 메시지 처리 실패 - PointFailureHistory 저장 중 오류: {}", message, e);
        }
    }

    @KafkaListener(topics = "board.created.dlt", groupId = "point-service")
    public void consumeBoardCreated(String message) {
        try {
            BoardCreatedEvent event = BoardCreatedEvent.fromJson(message);
            pointFailureHistoryRepository.save(PointFailureHistory.create(event.getUserId(), event.getActionType()));
        } catch (Exception e) {
            log.error("[게시글 작성] 포인트 적립 실패 DLT 메시지 처리 실패 - PointFailureHistory 저장 중 오류: {}", message, e);
        }
    }

    @KafkaListener(topics = "comment.created.dlt", groupId = "point-service")
    public void consumeCommentCreated(String message) {
        try {
            CommentCreatedEvent event = CommentCreatedEvent.fromJson(message);
            pointFailureHistoryRepository.save(PointFailureHistory.create(event.getUserId(), event.getActionType()));
        } catch (Exception e) {
            log.error("[댓글 작성] 포인트 적립 실패 DLT 메시지 처리 실패 - PointFailureHistory 저장 중 오류: {}", message, e);
        }
    }
}
