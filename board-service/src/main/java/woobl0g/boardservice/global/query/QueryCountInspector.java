package woobl0g.boardservice.global.query;

import lombok.RequiredArgsConstructor;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class QueryCountInspector implements StatementInspector {

    private final QueryCounter queryCounter;

    @Override
    public String inspect(String sql) {
        if (isInRequestScope()) {
            queryCounter.increateCount();
        }
        return sql;
    }

    private boolean isInRequestScope() {
        return Objects.nonNull(RequestContextHolder.getRequestAttributes());
    }
}
