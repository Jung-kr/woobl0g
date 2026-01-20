package woobl0g.userservice.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import woobl0g.userservice.auth.dto.SignUpRequestDto;
import woobl0g.userservice.global.exception.UserException;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.domain.User;
import woobl0g.userservice.user.dto.UserResponseDto;
import woobl0g.userservice.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("UserService 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    @DisplayName("회원가입 시 정상적으로 사용자가 생성되고 이벤트가 발행된다")
    void signUp() {
        // given
        SignUpRequestDto dto = new SignUpRequestDto("test@example.com", "테스터", "password123");
        User user = User.create(dto.getEmail(), dto.getName(), "encodedPassword");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // when
        userService.signUp(dto);

        // then
        verify(userRepository, times(1)).existsByEmail(dto.getEmail());
        verify(passwordEncoder, times(1)).encode(dto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(kafkaTemplate, times(1)).send(eq("user.signed-up"), anyString());
    }

    @Test
    @DisplayName("회원가입 시 중복 이메일이면 예외가 발생한다")
    void signUp_duplicateEmail() {
        // given
        SignUpRequestDto dto = new SignUpRequestDto("test@example.com", "테스터", "password123");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(dto))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.DUPLICATE_EMAIL);

        verify(userRepository, times(1)).existsByEmail(dto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 조회 시 정상적으로 조회된다")
    void getUser() {
        // given
        Long userId = 1L;
        User user = User.create("test@example.com", "테스터", "password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.getUser(userId);

        // then
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("테스터");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 조회 시 사용자가 없으면 예외가 발생한다")
    void getUser_notFound() {
        // given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.USER_NOT_FOUND);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("다중 사용자 조회 시 정상적으로 조회된다")
    void getUsers() {
        // given
        List<Long> userIds = List.of(1L, 2L);
        User user1 = User.create("test1@example.com", "테스터1", "password");
        User user2 = User.create("test2@example.com", "테스터2", "password");
        List<User> users = List.of(user1, user2);

        when(userRepository.findAllById(userIds)).thenReturn(users);

        // when
        List<UserResponseDto> result = userService.getUsers(userIds);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("test1@example.com");
        assertThat(result.get(1).getEmail()).isEqualTo("test2@example.com");
        verify(userRepository, times(1)).findAllById(userIds);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 시 정상적으로 조회된다")
    void getUserByEmail() {
        // given
        String email = "test@example.com";
        User user = User.create(email, "테스터", "password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUserByEmail(email);

        // then
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 시 사용자가 없으면 예외가 발생한다")
    void getUserByEmail_notFound() {
        // given
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.INVALID_CREDENTIALS);

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("인증용 사용자 조회 시 정상적으로 조회된다")
    void getUserForAuth() {
        // given
        Long userId = 1L;
        User user = User.create("test@example.com", "테스터", "password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUserForAuth(userId);

        // then
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("인증용 사용자 조회 시 사용자가 없으면 예외가 발생한다")
    void getUserForAuth_notFound() {
        // given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserForAuth(userId))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.USER_NOT_FOUND);

        verify(userRepository, times(1)).findById(userId);
    }
}
