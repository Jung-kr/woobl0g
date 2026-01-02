package woobl0g.userservice.user.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;
import woobl0g.userservice.user.domain.ActionType;
import woobl0g.userservice.user.dto.AddActivityScoreRequestDto;
import woobl0g.userservice.user.event.BoardCreatedEvent;
import woobl0g.userservice.user.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardCreatedEventConsumer {

    private final UserService userService;

    @KafkaListener(
            topics = "board.created",
            groupId = "user-service"
    )
    @RetryableTopic(
            attempts = "5",
            backOff = @BackOff(delay = 1000, multiplier = 2),
            dltTopicSuffix = ".dlt",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    public void consume(String message) {

        BoardCreatedEvent event = BoardCreatedEvent.fromJson(message);

        AddActivityScoreRequestDto addActivityScoreRequestDto = new AddActivityScoreRequestDto(event.getUserId(), ActionType.valueOf(event.getActionType()));
        userService.addActivityScore(addActivityScoreRequestDto);

        log.info("[게시글 생성] 활동 점수 적립 완료 - userId = {}", addActivityScoreRequestDto.getUserId());
    }
}
