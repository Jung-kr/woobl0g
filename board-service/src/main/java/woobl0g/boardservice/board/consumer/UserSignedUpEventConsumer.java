package woobl0g.boardservice.board.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import woobl0g.boardservice.board.dto.SavedUserRequestDto;
import woobl0g.boardservice.board.event.UserSignedUpEvent;
import woobl0g.boardservice.board.service.UserService;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.response.ResponseCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSignedUpEventConsumer {

    private final UserService userService;

    @KafkaListener(
            topics = "user.signed-up",
            groupId = "board-service"
    )
    public void consume(String message) {
        log.debug("회원가입 이벤트 수신: message={}", message);
        
        try {
            UserSignedUpEvent userSignedUpEvent = UserSignedUpEvent.fromJson(message);

            SavedUserRequestDto savedUserRequestDto = SavedUserRequestDto.of(
                    userSignedUpEvent.getUserId(),
                    userSignedUpEvent.getName(),
                    userSignedUpEvent.getEmail());

            userService.save(savedUserRequestDto);
            
            log.info("회원가입 이벤트 처리 완료: userId={}", userSignedUpEvent.getUserId());
        } catch (Exception e) {
            log.error("회원가입 이벤트 처리 실패: message={}", message, e);
            throw new BoardException(ResponseCode.USER_SYNC_FAILED);
        }
    }
}
