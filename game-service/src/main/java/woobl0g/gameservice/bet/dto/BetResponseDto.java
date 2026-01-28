package woobl0g.gameservice.bet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.gameservice.bet.domain.Bet;
import woobl0g.gameservice.bet.domain.BetAction;
import woobl0g.gameservice.bet.domain.BetType;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BetResponseDto {

    private Long betId;
    private String gameDate;
    private String matchup;
    private BetType betType;
    private Integer amount;
    private BetAction betAction;
    private LocalDateTime createdAt;

    public static BetResponseDto from(Bet bet) {
        return new BetResponseDto(
                bet.getBetId(),
                bet.getGame().getDate().toString(),
                bet.getGame().getHomeTeam().getTeamName() + " vs " + bet.getGame().getAwayTeam().getTeamName(),
                bet.getBetType(),
                bet.getAmount(),
                bet.getBetAction(),
                bet.getCreatedAt()
        );
    }
}
