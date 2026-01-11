package woobl0g.userservice.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import woobl0g.userservice.user.domain.Role;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, Role role) {
        Date nowDate = new Date();
        Date expiryDate = new Date(nowDate.getTime() + accessTokenValidity);

        log.debug("액세스 토큰 생성: userId={}, role={}", userId, role);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role.name())
                .issuedAt(nowDate)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date nowDate = new Date();
        Date expiryDate = new Date(nowDate.getTime() + refreshTokenValidity);

        log.debug("리프레시 토큰 생성: userId={}", userId);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(nowDate)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // JWT가 유효한지 서명·만료를 검증하고, 유효하면 payload를 디코딩해서 subject(userId)를 꺼내는 작업
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}
