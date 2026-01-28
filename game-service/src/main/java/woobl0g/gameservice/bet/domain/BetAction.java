package woobl0g.gameservice.bet.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BetAction {

    BET("배팅"),
    BET_CANCEL("취소"),
    BET_WIN("당첨"),
    BET_LOSE("낙첨"),
    BET_REFUND("환불");

    private final String description;
}
