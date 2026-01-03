package woobl0g.boardservice.board.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;
import woobl0g.boardservice.board.dto.SavedUserRequestDto;
import woobl0g.boardservice.board.event.UserSignedUpEvent;
import woobl0g.boardservice.board.service.UserService;

@Component
@RequiredArgsConstructor
public class UserSignedUpEventConsumer {

    private final UserService userService;

    @KafkaListener(
            topics = "user.signed-up",
            groupId = "board-service"
    )
    @RetryableTopic(
            attempts = "5",
            backOff = @BackOff(delay = 1000, multiplier = 2),
            dltTopicSuffix = ".dlt",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    public void consume(String message) {
        UserSignedUpEvent userSignedUpEvent = UserSignedUpEvent.fromJson(message);

        SavedUserRequestDto savedUserRequestDto = new SavedUserRequestDto(
                userSignedUpEvent.getUserId(),
                userSignedUpEvent.getName(),
                userSignedUpEvent.getEmail());

        userService.save(savedUserRequestDto);
    }
}
