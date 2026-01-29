package woobl0g.gameservice.game.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import woobl0g.gameservice.kbo.domain.GameStatus;
import woobl0g.gameservice.kbo.domain.SeriesType;
import woobl0g.gameservice.kbo.domain.Team;
import woobl0g.gameservice.kbo.dto.GameInfoDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Game 도메인 테스트")
class GameTest {

    @Test
    @DisplayName("경기 생성 시 정상적으로 생성된다")
    void create() {
        // given
        GameInfoDto gameInfoDto = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.of(2025, 1, 30),
                LocalTime.of(18, 0),
                Team.KIA,
                Team.LG,
                null,
                null,
                "TEST",
                "잠실",
                GameStatus.SCHEDULED,
                null
        );

        // when
        Game game = Game.create(gameInfoDto);

        // then
        assertThat(game.getGameKey()).isEqualTo("TEST_KEY");
        assertThat(game.getSeriesType()).isEqualTo(SeriesType.REGULAR_SEASON);
        assertThat(game.getDate()).isEqualTo(LocalDate.of(2025, 1, 30));
        assertThat(game.getTime()).isEqualTo(LocalTime.of(18, 0));
        assertThat(game.getAwayTeam()).isEqualTo(Team.KIA);
        assertThat(game.getHomeTeam()).isEqualTo(Team.LG);
        assertThat(game.getStadium()).isEqualTo("잠실");
        assertThat(game.getGameStatus()).isEqualTo(GameStatus.SCHEDULED);
        assertThat(game.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("경기 업데이트 시 변경된 필드가 업데이트된다")
    void update() {
        // given
        GameInfoDto originalGameInfo = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.of(2025, 1, 30),
                LocalTime.of(18, 0),
                Team.KIA,
                Team.LG,
                null,
                null,
                "TEST",
                "잠실",
                GameStatus.SCHEDULED,
                null
        );
        Game game = Game.create(originalGameInfo);

        GameInfoDto updatedGameInfo = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.of(2025, 1, 30),
                LocalTime.of(18, 30),
                Team.KIA,
                Team.LG,
                3,
                2,
                "TEST_UPDATE",
                "잠실",
                GameStatus.FINISHED,
                null
        );

        // when
        boolean isUpdated = game.update(updatedGameInfo);

        // then
        assertThat(isUpdated).isTrue();
        assertThat(game.getTime()).isEqualTo(LocalTime.of(18, 30));
        assertThat(game.getAwayScore()).isEqualTo(3);
        assertThat(game.getHomeScore()).isEqualTo(2);
        assertThat(game.getRelay()).isEqualTo("TEST_UPDATE");
        assertThat(game.getGameStatus()).isEqualTo(GameStatus.FINISHED);
    }

    @Test
    @DisplayName("경기 업데이트 시 변경 사항이 없으면 false를 반환한다")
    void update_noChange() {
        // given
        GameInfoDto originalGameInfo = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.of(2025, 1, 30),
                LocalTime.of(18, 0),
                Team.KIA,
                Team.LG,
                null,
                null,
                "TEST",
                "잠실",
                GameStatus.SCHEDULED,
                null
        );
        Game game = Game.create(originalGameInfo);

        GameInfoDto sameGameInfo = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.of(2025, 1, 30),
                LocalTime.of(18, 0),
                Team.KIA,
                Team.LG,
                null,
                null,
                "TEST",
                "잠실",
                GameStatus.SCHEDULED,
                null
        );

        // when
        boolean isUpdated = game.update(sameGameInfo);

        // then
        assertThat(isUpdated).isFalse();
    }

    @Test
    @DisplayName("배팅 오픈 시간은 경기 날짜 낮 12시이다")
    void getBettingOpenAt() {
        // given
        GameInfoDto gameInfoDto = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.of(2025, 1, 30),
                LocalTime.of(18, 0),
                Team.KIA,
                Team.LG,
                null,
                null,
                "TEST",
                "잠실",
                GameStatus.SCHEDULED,
                null
        );
        Game game = Game.create(gameInfoDto);

        // when
        LocalDateTime bettingOpenAt = game.getBettingOpenAt();

        // then
        assertThat(bettingOpenAt).isEqualTo(LocalDateTime.of(2025, 1, 30, 12, 0));
    }

    @Test
    @DisplayName("배팅 마감 시간은 경기 시작 30분 전이다")
    void getBettingCloseAt() {
        // given
        GameInfoDto gameInfoDto = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.of(2025, 1, 30),
                LocalTime.of(18, 0),
                Team.KIA,
                Team.LG,
                null,
                null,
                "TEST",
                "잠실",
                GameStatus.SCHEDULED,
                null
        );
        Game game = Game.create(gameInfoDto);

        // when
        LocalDateTime bettingCloseAt = game.getBettingCloseAt();

        // then
        assertThat(bettingCloseAt).isEqualTo(LocalDateTime.of(2025, 1, 30, 17, 30));
    }

    @Test
    @DisplayName("경기 시간이 null이면 배팅 마감 시간도 null이다")
    void getBettingCloseAt_nullTime() {
        // given
        GameInfoDto gameInfoDto = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.of(2025, 1, 30),
                null,
                Team.KIA,
                Team.LG,
                null,
                null,
                "TEST",
                "잠실",
                GameStatus.SCHEDULED,
                null
        );
        Game game = Game.create(gameInfoDto);

        // when
        LocalDateTime bettingCloseAt = game.getBettingCloseAt();

        // then
        assertThat(bettingCloseAt).isNull();
    }

    @Test
    @DisplayName("배팅 마감 여부 확인 시 마감 시간이 null이면 false를 반환한다")
    void isBettingClosed_nullCloseAt() {
        // given
        GameInfoDto gameInfoDto = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.of(2025, 1, 30),
                null,
                Team.KIA,
                Team.LG,
                null,
                null,
                "TEST",
                "잠실",
                GameStatus.SCHEDULED,
                null
        );
        Game game = Game.create(gameInfoDto);

        // when
        boolean isClosed = game.isBettingClosed();

        // then
        assertThat(isClosed).isFalse();
    }
}
