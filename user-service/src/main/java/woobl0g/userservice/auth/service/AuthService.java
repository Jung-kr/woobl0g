package woobl0g.userservice.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.userservice.auth.dto.LoginRequestDto;
import woobl0g.userservice.auth.dto.RefreshTokenRequestDto;
import woobl0g.userservice.auth.dto.SignUpRequestDto;
import woobl0g.userservice.auth.dto.TokenResponseDto;
import woobl0g.userservice.global.exception.UserException;
import woobl0g.userservice.global.jwt.JwtTokenProvider;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.domain.User;
import woobl0g.userservice.user.service.UserService;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void signUp(SignUpRequestDto dto) {
        log.info("회원가입 시도: email={}", dto.getEmail());
        userService.signUp(dto);
        log.info("회원가입 완료: email={}", dto.getEmail());
    }

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto dto) {
        log.info("로그인 시도: email={}", dto.getEmail());
        
        User user = userService.getUserByEmail(dto.getEmail());
        user.validatePassword(dto.getPassword(), passwordEncoder);

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        // Refresh Token 저장 (1인 1세션, 기존 토큰 덮어쓰기)
        // Key: "refresh_token:{userId}", Value: refreshToken, TTL: 7일
        redisTemplate.opsForValue().set(
                "refresh_token:" + user.getUserId(),
                refreshToken,
                7,
                TimeUnit.DAYS
        );

        log.info("로그인 성공: userId={}", user.getUserId());
        return TokenResponseDto.of(accessToken, refreshToken, "Bearer");
    }

    @Transactional(readOnly = true)
    public TokenResponseDto getRefreshToken(RefreshTokenRequestDto dto) {
        String refreshToken = dto.getRefreshToken();

        // 1. Refresh Token 유효성 검증 & userId 추출
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("토큰 갱신 실패 - 유효하지 않은 토큰");
            throw new UserException(ResponseCode.INVALID_TOKEN);
        }
        Long userId = jwtTokenProvider.getUserIdFromToken(dto.getRefreshToken());

        // 2. Redis에 저장된 Refresh Token 조회
        String storedRefreshToken = redisTemplate.opsForValue().get("refresh_token:" + userId);
        // 3-1. Redis에 토큰이 없는 경우 (로그아웃 or 만료)
        if (storedRefreshToken == null) {
            log.warn("토큰 갱신 실패 - 토큰 없음: userId={}", userId);
            throw new UserException(ResponseCode.TOKEN_NOT_FOUND);
        }

        // 3-2. 토큰 불일치 → Refresh Token 재사용 공격 감지 -> 해당 사용자의 모든 토큰 무효화
        if (!storedRefreshToken.equals(refreshToken)) {
            log.error("토큰 재사용 감지 - 모든 토큰 무효화: userId={}", userId);
            redisTemplate.delete("refresh_token:" + userId);
            throw new UserException(ResponseCode.TOKEN_REUSE_DETECTED);
        }

        // 4. 검증 완료 -> 새로운 토큰 발급
        User user = userService.getUserForAuth(userId);
        String newAccessToken = jwtTokenProvider.createAccessToken(userId, user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        // 5. 새로운 Refresh Token을 Redis에 저장 (기존 토큰 덮어씌어짐)
        redisTemplate.opsForValue().set(
                "refresh_token:" + userId,
                newRefreshToken,
                7,
                TimeUnit.DAYS
        );

        log.info("토큰 갱신 성공: userId={}", userId);
        return TokenResponseDto.of(newAccessToken, newRefreshToken, "Bearer");
    }
}
