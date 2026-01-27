package woobl0g.gameservice.kbo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameStatus {

    SCHEDULED("예정"),
    IN_PROGRESS("진행중"),
    FINISHED("종료"),
    CANCELLED("취소");

    private final String description;
}
