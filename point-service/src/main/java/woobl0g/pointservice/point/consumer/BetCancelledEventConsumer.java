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
import woobl0g.pointservice.point.event.BetCancelledEvent;
import woobl0g.pointservice.point.service.PointService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BetCancelledEventConsumer {

    private final PointService pointService;

    @KafkaListener(
            topics = "bet.cancelled",
            groupId = "point-service"
    )
    @RetryableTopic(
            attempts = "5",
            backOff = @BackOff(delay = 1000, multiplier = 2),
            dltTopicSuffix = ".dlt",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    public void consume(String message) {
        log.debug("베팅 취소 이벤트 수신: message={}", message);

        BetCancelledEvent event = BetCancelledEvent.fromJson(message);

        AddPointRequestDto addPointRequestDto = AddPointRequestDto.of(event.getUserId(), PointActionType.valueOf(event.getActionType()), event.getAmount());
        pointService.addPoints(addPointRequestDto);

        log.info("베팅 취소 포인트 적립 완료: userId={}", addPointRequestDto.getUserId());
    }
}
