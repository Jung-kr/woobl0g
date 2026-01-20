package woobl0g.userservice.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import woobl0g.userservice.auth.dto.LoginRequestDto;
import woobl0g.userservice.auth.dto.RefreshTokenRequestDto;
import woobl0g.userservice.auth.dto.SignUpRequestDto;
import woobl0g.userservice.auth.dto.TokenResponseDto;
import woobl0g.userservice.global.exception.UserException;
import woobl0g.userservice.global.jwt.JwtTokenProvider;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.domain.Role;
import woobl0g.userservice.user.domain.User;
import woobl0g.userservice.user.service.UserService;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthService 테스트")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("회원가입 시 UserService를 호출한다")
    void signUp() {
        // given
        SignUpRequestDto dto = new SignUpRequestDto("test@example.com", "테스터", "password123");

        doNothing().when(userService).signUp(dto);

        // when
        authService.signUp(dto);

        // then
        verify(userService, times(1)).signUp(dto);
    }

    @Test
    @DisplayName("로그인 시 정상적으로 토큰이 발급된다")
    void login() {
        // given
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "password123");
        String encodedPassword = "encodedPassword";
        User user = User.create("test@example.com", "테스터", encodedPassword);
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        when(userService.getUserByEmail(dto.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(dto.getPassword(), encodedPassword)).thenReturn(true);
        when(jwtTokenProvider.createAccessToken(any(), any(Role.class))).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn(refreshToken);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // when
        TokenResponseDto result = authService.login(dto);

        // then
        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        verify(userService, times(1)).getUserByEmail(dto.getEmail());
        verify(passwordEncoder, times(1)).matches(dto.getPassword(), encodedPassword);
        verify(jwtTokenProvider, times(1)).createAccessToken(any(), any(Role.class));
        verify(jwtTokenProvider, times(1)).createRefreshToken(any());
        verify(valueOperations, times(1)).set(anyString(), eq(refreshToken), eq(7L), eq(TimeUnit.DAYS));
    }

    @Test
    @DisplayName("토큰 갱신 시 정상적으로 새 토큰이 발급된다")
    void getRefreshToken() {
        // given
        String oldRefreshToken = "oldRefreshToken";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";
        Long userId = 1L;
        RefreshTokenRequestDto dto = new RefreshTokenRequestDto(oldRefreshToken);
        User user = User.create("test@example.com", "테스터", "password");

        when(jwtTokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(oldRefreshToken)).thenReturn(userId);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:" + userId)).thenReturn(oldRefreshToken);
        when(userService.getUserForAuth(userId)).thenReturn(user);
        when(jwtTokenProvider.createAccessToken(userId, user.getRole())).thenReturn(newAccessToken);
        when(jwtTokenProvider.createRefreshToken(userId)).thenReturn(newRefreshToken);
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // when
        TokenResponseDto result = authService.getRefreshToken(dto);

        // then
        assertThat(result.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(result.getRefreshToken()).isEqualTo(newRefreshToken);
        verify(jwtTokenProvider, times(1)).validateToken(oldRefreshToken);
        verify(valueOperations, times(1)).get("refresh_token:" + userId);
        verify(userService, times(1)).getUserForAuth(userId);
    }

    @Test
    @DisplayName("토큰 갱신 시 유효하지 않은 토큰이면 예외가 발생한다")
    void getRefreshToken_invalidToken() {
        // given
        String refreshToken = "invalidToken";
        RefreshTokenRequestDto dto = new RefreshTokenRequestDto(refreshToken);

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.getRefreshToken(dto))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.INVALID_TOKEN);

        verify(jwtTokenProvider, times(1)).validateToken(refreshToken);
    }

    @Test
    @DisplayName("토큰 갱신 시 Redis에 토큰이 없으면 예외가 발생한다")
    void getRefreshToken_tokenNotFound() {
        // given
        String refreshToken = "validToken";
        Long userId = 1L;
        RefreshTokenRequestDto dto = new RefreshTokenRequestDto(refreshToken);

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:" + userId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> authService.getRefreshToken(dto))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.TOKEN_NOT_FOUND);

        verify(valueOperations, times(1)).get("refresh_token:" + userId);
    }

    @Test
    @DisplayName("토큰 갱신 시 토큰이 불일치하면 재사용 감지 예외가 발생한다")
    void getRefreshToken_tokenReuse() {
        // given
        String refreshToken = "token1";
        String storedToken = "token2";
        Long userId = 1L;
        RefreshTokenRequestDto dto = new RefreshTokenRequestDto(refreshToken);

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:" + userId)).thenReturn(storedToken);
        when(redisTemplate.delete("refresh_token:" + userId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.getRefreshToken(dto))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.TOKEN_REUSE_DETECTED);

        verify(redisTemplate, times(1)).delete("refresh_token:" + userId);
    }
}
