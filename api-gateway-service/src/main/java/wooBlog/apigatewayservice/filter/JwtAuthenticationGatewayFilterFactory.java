package wooBlog.apigatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import wooBlog.apigatewayservice.error.GatewayErrorResponseWriter;
import wooBlog.apigatewayservice.jwt.JwtTokenProvider;

@Slf4j
@Component
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    private final JwtTokenProvider jwtTokenProvider;
    private final GatewayErrorResponseWriter gatewayErrorResponseWriter;

    public JwtAuthenticationGatewayFilterFactory(JwtTokenProvider jwtTokenProvider, GatewayErrorResponseWriter gatewayErrorResponseWriter) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
        this.gatewayErrorResponseWriter = gatewayErrorResponseWriter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            // Request Header에서 토큰 가져오기
            String token = jwtTokenProvider.resolveToken(
                    exchange.getRequest().getHeaders().getFirst("Authorization")
            );
            log.debug("JWT 토큰 추출: token={}", token != null ? "존재" : "없음");

            // 토큰이 없을 경우 401 Unauthorized로 응답
            if (token == null) {
                log.warn("JWT 인증 실패 - 토큰 없음: path={}", exchange.getRequest().getPath());
                return gatewayErrorResponseWriter.onError(exchange, HttpStatus.UNAUTHORIZED, "TOKEN_MISSING");
            }


            try {
                Claims claims = jwtTokenProvider.parseClaims(token);

                String userId = claims.getSubject();
                String role = claims.get("role").toString();

                log.debug("JWT 인증 성공: userId={}, role={}", userId, role);

                // Payload를 X-User-Id 헤더에 담아서 Request 전달
                return chain.filter(
                        exchange.mutate()
                                .request(
                                        exchange.getRequest()
                                                .mutate()
                                                .header("X-User-Id", userId)
                                                .header("X-User-Role", role)
                                                .build()
                                )
                                .build()
                );
            } catch (ExpiredJwtException e) {  //유효한 토큰이지만 만료 → 재발급 필요
                log.warn("JWT 인증 실패 - 토큰 만료: path={}", exchange.getRequest().getPath());
                return gatewayErrorResponseWriter.onError(exchange, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
            } catch (JwtException | IllegalArgumentException e) {  // 위조, 변조, 형식 오류 → 즉시 차단
                log.warn("JWT 인증 실패 - 유효하지 않은 토큰: path={}, error={}", 
                        exchange.getRequest().getPath(), e.getMessage());
                return gatewayErrorResponseWriter.onError(exchange, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
            }
        };
    }

    public static class Config {

    }
}
