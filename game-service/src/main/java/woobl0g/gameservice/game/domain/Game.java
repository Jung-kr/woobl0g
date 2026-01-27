package woobl0g.gameservice.game.domain;

import jakarta.persistence.*;
import lombok.*;
import woobl0g.gameservice.kbo.domain.CancellationReason;
import woobl0g.gameservice.kbo.domain.GameStatus;
import woobl0g.gameservice.kbo.domain.SeriesType;
import woobl0g.gameservice.kbo.domain.Team;
import woobl0g.gameservice.kbo.dto.GameInfoDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(unique = true, nullable = false)
    private String gameKey;

    @Enumerated(EnumType.STRING)
    private SeriesType seriesType;

    private LocalDate date;
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    private Team awayTeam;

    @Enumerated(EnumType.STRING)
    private Team homeTeam;

    private Integer awayScore;
    private Integer homeScore;

    private String relay;
    private String stadium;

    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @Enumerated(EnumType.STRING)
    private CancellationReason cancellationReason;

    private LocalDateTime createdAt;

    private Game(String gameKey, SeriesType seriesType, LocalDate date, LocalTime time, Team awayTeam, Team homeTeam, Integer awayScore, Integer homeScore, String relay, String stadium, GameStatus gameStatus, CancellationReason cancellationReason) {
        this.gameKey = gameKey;
        this.seriesType = seriesType;
        this.date = date;
        this.time = time;
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.awayScore = awayScore;
        this.homeScore = homeScore;
        this.relay = relay;
        this.stadium = stadium;
        this.gameStatus = gameStatus;
        this.cancellationReason = cancellationReason;
        this.createdAt = LocalDateTime.now();
    }

    public static Game create(GameInfoDto gameInfoDto) {
        return new Game(
                gameInfoDto.getGameKey(),
                gameInfoDto.getSeriesType(),
                gameInfoDto.getDate(),
                gameInfoDto.getTime(),
                gameInfoDto.getAwayTeam(),
                gameInfoDto.getHomeTeam(),
                gameInfoDto.getAwayScore(),
                gameInfoDto.getHomeScore(),
                gameInfoDto.getRelay(),
                gameInfoDto.getStadium(),
                gameInfoDto.getGameStatus(),
                gameInfoDto.getCancellationReason()
        );
    }

    public boolean update(GameInfoDto gameInfoDto) {
        boolean isUpdated =
                !Objects.equals(this.time, gameInfoDto.getTime())
                || !Objects.equals(this.awayScore, gameInfoDto.getAwayScore())
                || !Objects.equals(this.homeScore, gameInfoDto.getHomeScore())
                || !Objects.equals(this.stadium, gameInfoDto.getStadium())
                || !Objects.equals(this.relay, gameInfoDto.getRelay())
                || !Objects.equals(this.gameStatus, gameInfoDto.getGameStatus())
                || !Objects.equals(this.cancellationReason, gameInfoDto.getCancellationReason());

        if (!isUpdated) {
            return false;
        }

        this.time = gameInfoDto.getTime();
        this.awayScore = gameInfoDto.getAwayScore();
        this.homeScore = gameInfoDto.getHomeScore();
        this.stadium = gameInfoDto.getStadium();
        this.relay = gameInfoDto.getRelay();
        this.gameStatus = gameInfoDto.getGameStatus();
        this.cancellationReason = gameInfoDto.getCancellationReason();

        return true;
    }
}
