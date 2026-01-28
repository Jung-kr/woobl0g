package woobl0g.gameservice.bet.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BetType {

    HOME_WIN("홈팀 승리"),
    AWAY_WIN("원정팀 승리"),
    DRAW("무승부");

    private final String description;
}
