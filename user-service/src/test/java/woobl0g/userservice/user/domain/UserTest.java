package woobl0g.userservice.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import woobl0g.userservice.global.exception.UserException;
import woobl0g.userservice.global.response.ResponseCode;

import static org.assertj.core.api.Assertions.*;

@DisplayName("User 도메인 테스트")
class UserTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("사용자 생성 시 기본 역할은 USER이다")
    void create() {
        // given
        String email = "test@example.com";
        String name = "테스터";
        String password = "password123";

        // when
        User user = User.create(email, name, password);

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("비밀번호 검증 시 일치하면 예외가 발생하지 않는다")
    void validatePassword_success() {
        // given
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = User.create("test@example.com", "테스터", encodedPassword);

        // when & then
        assertThatCode(() -> user.validatePassword(rawPassword, passwordEncoder))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("비밀번호 검증 시 불일치하면 예외가 발생한다")
    void validatePassword_fail() {
        // given
        String rawPassword = "password123";
        String wrongPassword = "wrongPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = User.create("test@example.com", "테스터", encodedPassword);

        // when & then
        assertThatThrownBy(() -> user.validatePassword(wrongPassword, passwordEncoder))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.INVALID_CREDENTIALS);
    }
}
