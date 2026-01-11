package wooBlog.apigatewayservice.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class GatewayErrorResponseWriter {

    public Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String code) {
        log.warn("Gateway 에러 응답: status={}, code={}, path={}", 
                status, code, exchange.getRequest().getPath());
        
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String body = String.format(
                "{\"code\":\"%s\"}",
                code
        );
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
