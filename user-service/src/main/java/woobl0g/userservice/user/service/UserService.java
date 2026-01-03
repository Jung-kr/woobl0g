package woobl0g.userservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.userservice.global.exception.UserException;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.client.PointClient;
import woobl0g.userservice.user.domain.User;
import woobl0g.userservice.user.dto.AddActivityScoreRequestDto;
import woobl0g.userservice.user.dto.SignUpRequestDto;
import woobl0g.userservice.user.dto.UserResponseDto;
import woobl0g.userservice.user.event.UserSignedUpEvent;
import woobl0g.userservice.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PointClient pointClient;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void signUp(SignUpRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserException(ResponseCode.DUPLICATE_EMAIL);
        }

        User user = User.create(dto.getEmail(), dto.getName(), dto.getPassword());
        User savedUser = userRepository.save(user);

        // 회원가입시 포인트 적립
        pointClient.addPoints(savedUser.getUserId(), "SIGN_UP");

        // 회원가입 이벤트 발행 -> board-service에 사용자 데이터 동기화 위함
        UserSignedUpEvent userSignedUpEvent = new UserSignedUpEvent(savedUser.getUserId(), savedUser.getName(), savedUser.getEmail());
        kafkaTemplate.send("user.signed-up", userSignedUpEvent.toJson());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ResponseCode.USER_NOT_FOUND));

        return UserResponseDto.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        return users.stream()
                .map(user -> new UserResponseDto(
                        user.getUserId(),
                        user.getEmail(),
                        user.getName()
                ))
                .toList();
    }

    @Transactional
    public void addActivityScore(AddActivityScoreRequestDto addActivityScoreRequestDto) {

        User user = userRepository.findById(addActivityScoreRequestDto.getUserId())
                .orElseThrow(() -> new UserException(ResponseCode.USER_NOT_FOUND));

        int scoreChange = addActivityScoreRequestDto.getActionType().getActivityScore();
        String reason = addActivityScoreRequestDto.getActionType().name();

        user.addActivityScore(scoreChange);

        // 활동 점수 기록 저장
        ActivityScoreHistory history = ActivityScoreHistory.create(
                user.getUserId(),
                scoreChange,
                reason
        );
        activityScoreHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<ActivityScoreHistoryResponseDto> getActivityScoreHistory(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserException(ResponseCode.USER_NOT_FOUND);
        }

        List<ActivityScoreHistory> histories = activityScoreHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return histories.stream()
                .map(ActivityScoreHistoryResponseDto::from)
                .toList();
    }
}
