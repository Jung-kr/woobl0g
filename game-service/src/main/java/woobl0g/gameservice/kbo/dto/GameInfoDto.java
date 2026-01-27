package woobl0g.gameservice.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.gameservice.kbo.domain.CancellationReason;
import woobl0g.gameservice.kbo.domain.GameStatus;
import woobl0g.gameservice.kbo.domain.SeriesType;
import woobl0g.gameservice.kbo.domain.Team;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class GameInfoDto {

    private String gameKey;
    private SeriesType seriesType;
    private LocalDate date;
    private LocalTime time;
    private Team awayTeam;
    private Team homeTeam;
    private Integer awayScore;
    private Integer homeScore;
    private String relay;
    private String stadium;
    private GameStatus gameStatus;
    private CancellationReason cancellationReason;

    public static GameInfoDto of(String gameKey, SeriesType seriesType, LocalDate date, LocalTime time, Team awayTeam, Team homeTeam, Integer awayScore, Integer homeScore, String relay, String stadium, GameStatus gameStatus, CancellationReason cancellationReason) {
        return new GameInfoDto(
                gameKey,
                seriesType,
                date,
                time,
                awayTeam,
                homeTeam,
                awayScore,
                homeScore,
                relay,
                stadium,
                gameStatus,
                cancellationReason
        );
    }
}
