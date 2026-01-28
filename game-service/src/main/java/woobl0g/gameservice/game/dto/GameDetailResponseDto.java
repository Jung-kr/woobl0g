package woobl0g.gameservice.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.gameservice.bet.domain.BetType;
import woobl0g.gameservice.game.domain.Game;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GameDetailResponseDto {

    private Long gameId;
    private String date;
    private String time;
    private String homeTeam;
    private String awayTeam;
    private Boolean isBettingOpen;

    private Double homeWinOdds;
    private Double awayWinOdds;
    private Double drawOdds;

    private BetType myBetType;
    private Integer myTotalAmount;
    private Boolean canBetMore;

    public static GameDetailResponseDto of(Game game, Double homeWinOdds, Double awayWinOdds, Double drawOdds, BetType myBetType, Integer myTotalAmount) {
        boolean isBettingOpen = !game.isBettingClosed()
                && LocalDateTime.now().isAfter(game.getBettingOpenAt());

        return new GameDetailResponseDto(
                game.getGameId(),
                game.getDate().toString(),
                game.getTime() != null ? game.getTime().toString() : null,
                game.getHomeTeam().getTeamName(),
                game.getAwayTeam().getTeamName(),
                isBettingOpen,
                homeWinOdds,
                awayWinOdds,
                drawOdds,
                myBetType,
                myTotalAmount,
                myTotalAmount != null ? myTotalAmount < 1000 : null
        );
    }
}
