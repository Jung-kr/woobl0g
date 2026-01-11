package woobl0g.userservice.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void signUp(SignUpRequestDto dto, PasswordEncoder passwordEncoder) {
        log.debug("회원가입 처리 시작: email={}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("회원가입 실패 - 중복 이메일: email={}", dto.getEmail());
            throw new UserException(ResponseCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.create(dto.getEmail(), dto.getName(), encodedPassword);
        User savedUser = userRepository.save(user);

        // 회원가입 이벤트 발행 -> board-service에 사용자 데이터 동기화 & point-service에 point 적립
        UserSignedUpEvent userSignedUpEvent = UserSignedUpEvent.of(savedUser.getUserId(), savedUser.getName(), savedUser.getEmail(), "SIGN_UP");
        kafkaTemplate.send("user.signed-up", userSignedUpEvent.toJson());
        
        log.info("회원가입 완료 및 이벤트 발행: userId={}", savedUser.getUserId());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {
        log.debug("사용자 조회: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ResponseCode.USER_NOT_FOUND));

        return UserResponseDto.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers(List<Long> userIds) {
        log.debug("다중 사용자 조회: count={}", userIds.size());
        
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
