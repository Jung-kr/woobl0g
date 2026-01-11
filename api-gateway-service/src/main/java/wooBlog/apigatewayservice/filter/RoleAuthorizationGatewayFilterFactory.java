package wooBlog.apigatewayservice.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import wooBlog.apigatewayservice.error.GatewayErrorResponseWriter;

@Slf4j
@Component
public class RoleAuthorizationGatewayFilterFactory extends AbstractGatewayFilterFactory<RoleAuthorizationGatewayFilterFactory.Config> {

    private final GatewayErrorResponseWriter gatewayErrorResponseWriter;

    public RoleAuthorizationGatewayFilterFactory(GatewayErrorResponseWriter gatewayErrorResponseWriter) {
        super(Config.class);
        this.gatewayErrorResponseWriter = gatewayErrorResponseWriter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String role = exchange.getRequest().getHeaders().getFirst("X-User-Role");

            log.debug("권한 검증: requiredRole={}, userRole={}", config.requiredRole, role);

            if (!config.requiredRole.equals(role)) {
                log.warn("권한 검증 실패 - 권한 부족: path={}, requiredRole={}, userRole={}", 
                        exchange.getRequest().getPath(), config.requiredRole, role);
                return gatewayErrorResponseWriter.onError(exchange, HttpStatus.FORBIDDEN, "ROLE_FORBIDDEN");
            }

            log.debug("권한 검증 성공: role={}", role);
            return chain.filter(exchange);
        };
    }

    @Getter
    @Setter
    public static class Config {
        private String requiredRole;
    }
}
