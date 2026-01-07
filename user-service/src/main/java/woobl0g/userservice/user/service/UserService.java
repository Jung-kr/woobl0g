package woobl0g.userservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.userservice.auth.dto.SignUpRequestDto;
import woobl0g.userservice.global.exception.UserException;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.domain.User;
import woobl0g.userservice.user.dto.UserResponseDto;
import woobl0g.userservice.user.event.UserSignedUpEvent;
import woobl0g.userservice.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void signUp(SignUpRequestDto dto, PasswordEncoder passwordEncoder) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserException(ResponseCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.create(dto.getEmail(), dto.getName(), encodedPassword);
        User savedUser = userRepository.save(user);

        // 회원가입 이벤트 발행 -> board-service에 사용자 데이터 동기화 & point-service에 point 적립
        UserSignedUpEvent userSignedUpEvent = new UserSignedUpEvent(savedUser.getUserId(), savedUser.getName(), savedUser.getEmail(), "SIGN_UP");
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

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ResponseCode.INVALID_CREDENTIALS));
    }

    @Transactional(readOnly = true)
    public User getUserForAuth(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ResponseCode.USER_NOT_FOUND));
    }
}
