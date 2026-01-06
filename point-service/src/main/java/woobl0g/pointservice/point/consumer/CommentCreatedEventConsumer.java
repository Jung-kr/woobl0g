package woobl0g.pointservice.point.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;
import woobl0g.pointservice.point.domain.PointActionType;
import woobl0g.pointservice.point.dto.AddPointRequestDto;
import woobl0g.pointservice.point.event.BoardCreatedEvent;
import woobl0g.pointservice.point.event.CommentCreatedEvent;
import woobl0g.pointservice.point.service.PointService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentCreatedEventConsumer {

    private final PointService pointService;

    @KafkaListener(
            topics = "comment.created",
            groupId = "point-service"
    )
    @RetryableTopic(
            attempts = "5",
            backOff = @BackOff(delay = 1000, multiplier = 2),
            dltTopicSuffix = ".dlt",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    public void consume(String message) {
        CommentCreatedEvent event = CommentCreatedEvent.fromJson(message);

        AddPointRequestDto addPointRequestDto = new AddPointRequestDto(event.getUserId(), PointActionType.valueOf(event.getActionType()));
        pointService.addPoints(addPointRequestDto);

        log.info("[댓글 생성] 포인트 적립 완료 - userId = {}", addPointRequestDto.getUserId());
    }
}
