package woobl0g.boardservice.board.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import woobl0g.boardservice.board.dto.SavedUserRequestDto;
import woobl0g.boardservice.board.event.UserSignedUpEvent;
import woobl0g.boardservice.board.service.UserService;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.response.ResponseCode;

@Component
@RequiredArgsConstructor
public class UserSignedUpEventConsumer {

    private final UserService userService;

    @KafkaListener(
            topics = "user.signed-up",
            groupId = "board-service"
    )
    public void consume(String message) {
        try {
            UserSignedUpEvent userSignedUpEvent = UserSignedUpEvent.fromJson(message);

            SavedUserRequestDto savedUserRequestDto = SavedUserRequestDto.of(
                    userSignedUpEvent.getUserId(),
                    userSignedUpEvent.getName(),
                    userSignedUpEvent.getEmail());

            userService.save(savedUserRequestDto);
        } catch (Exception e) {
            throw new BoardException(ResponseCode.USER_SYNC_FAILED);
        }
    }
}
