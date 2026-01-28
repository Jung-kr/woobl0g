package woobl0g.gameservice.bet.dto;

import lombok.Getter;
import woobl0g.gameservice.bet.domain.BetType;

@Getter
public class PlaceBetRequestDto {

    private Long gameId;
    private BetType betType;
    private Integer betAmount;
}
