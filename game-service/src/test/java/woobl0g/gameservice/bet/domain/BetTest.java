package woobl0g.gameservice.bet.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import woobl0g.gameservice.game.domain.Game;
import woobl0g.gameservice.kbo.domain.GameStatus;
import woobl0g.gameservice.kbo.domain.SeriesType;
import woobl0g.gameservice.kbo.domain.Team;
import woobl0g.gameservice.kbo.dto.GameInfoDto;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Bet 도메인 테스트")
class BetTest {

    @Test
    @DisplayName("배팅 생성 시 정상적으로 생성된다")
    void create() {
        // given
        Long userId = 1L;
        GameInfoDto gameInfoDto = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.now(),
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
        BetType betType = BetType.HOME_WIN;
        Integer amount = -100;
        BetAction betAction = BetAction.BET;

        // when
        Bet bet = Bet.create(userId, game, betType, amount, betAction);

        // then
        assertThat(bet.getUserId()).isEqualTo(userId);
        assertThat(bet.getGame()).isEqualTo(game);
        assertThat(bet.getBetType()).isEqualTo(betType);
        assertThat(bet.getAmount()).isEqualTo(amount);
        assertThat(bet.getBetAction()).isEqualTo(betAction);
        assertThat(bet.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("배팅 취소 시 양수 금액으로 생성된다")
    void create_cancel() {
        // given
        Long userId = 1L;
        GameInfoDto gameInfoDto = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                LocalDate.now(),
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
        BetType betType = BetType.HOME_WIN;
        Integer amount = 100;
        BetAction betAction = BetAction.BET_CANCEL;

        // when
        Bet bet = Bet.create(userId, game, betType, amount, betAction);

        // then
        assertThat(bet.getAmount()).isEqualTo(100);
        assertThat(bet.getBetAction()).isEqualTo(BetAction.BET_CANCEL);
    }
}
