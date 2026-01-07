package woobl0g.userservice.auth.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(SignUpRequestDto dto) {
        userService.signUp(dto, passwordEncoder);
    }

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto dto) {
        User user = userService.getUserByEmail(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UserException(ResponseCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        // [Redis] userId를 key로 refreshToken 저장

        return new TokenResponseDto(accessToken, refreshToken, "Bearer");
    }

    @Transactional(readOnly = true)
    public TokenResponseDto getRefreshToken(RefreshTokenRequestDto dto) {
        // Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(dto.getRefreshToken())) {
            throw new UserException(ResponseCode.INVALID_TOKEN);
        }

        // [Redis] 저장된 Refresh Token 조회 후 요청 토큰과 저장 토큰 비교 -> 같지 않다면 예외

        Long userId = jwtTokenProvider.getUserIdFromToken(dto.getRefreshToken());
        String accessToken = jwtTokenProvider.createAccessToken(userId);

        return new TokenResponseDto(accessToken, dto.getRefreshToken(), "Bearer");
    }
}
