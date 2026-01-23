package woobl0g.boardservice.global.query;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class QueryCounter {

    private int count;

    public void increateCount() {
        count++;
    }
}
