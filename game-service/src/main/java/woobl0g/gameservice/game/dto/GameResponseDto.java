package woobl0g.gameservice.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.gameservice.game.domain.Game;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class GameResponseDto {

    private Long gameId;
    private LocalDate date;
    private LocalTime time;
    private String homeTeam;
    private String awayTeam;
    private String stadium;
    private Integer awayScore;
    private Integer homeScore;
    private Boolean isBettingOpen;

    public static GameResponseDto from(Game game, Boolean isBettingOpen) {
        return new GameResponseDto(
                game.getGameId(),
                game.getDate(),
                game.getTime(),
                game.getHomeTeam().getTeamName(),
                game.getAwayTeam().getTeamName(),
                game.getStadium(),
                game.getAwayScore(),
                game.getHomeScore(),
                isBettingOpen
        );
    }
}
