package woobl0g.gameservice.bet.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BetAction {

    BET("배팅"),
    CANCEL("취소"),
    WIN("당첨"),
    LOSE("낙첨"),
    REFUND("환불");

    private final String description;
}
