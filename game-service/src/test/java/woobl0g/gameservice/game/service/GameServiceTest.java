package woobl0g.gameservice.game.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import woobl0g.gameservice.bet.domain.BetType;
import woobl0g.gameservice.bet.service.OddsService;
import woobl0g.gameservice.game.domain.Game;
import woobl0g.gameservice.game.dto.GameDetailResponseDto;
import woobl0g.gameservice.game.dto.GameResponseDto;
import woobl0g.gameservice.game.dto.UpsertGameResponseDto;
import woobl0g.gameservice.game.repository.GameRepository;
import woobl0g.gameservice.global.exception.GameException;
import woobl0g.gameservice.global.response.ResponseCode;
import woobl0g.gameservice.kbo.domain.GameStatus;
import woobl0g.gameservice.kbo.domain.SeriesType;
import woobl0g.gameservice.kbo.domain.Team;
import woobl0g.gameservice.kbo.dto.GameInfoDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("GameService 테스트")
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private OddsService oddsService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("경기 정보 UPSERT 시 신규 경기가 저장된다")
    void upsertGames_newGames() {
        // given
        GameInfoDto gameInfoDto = new GameInfoDto(
                "TEST_KEY_1",
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
        List<GameInfoDto> gameInfoDtoList = List.of(gameInfoDto);

        when(gameRepository.findByGameKeyIn(anyList())).thenReturn(List.of());
        when(gameRepository.saveAll(anyList())).thenReturn(null);

        // when
        UpsertGameResponseDto result = gameService.upsertGames(gameInfoDtoList);

        // then
        assertThat(result.getCollectedCount()).isEqualTo(1);
        assertThat(result.getSavedCount()).isEqualTo(1);
        assertThat(result.getModifiedCount()).isZero();
        verify(gameRepository, times(1)).findByGameKeyIn(anyList());
        verify(gameRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("경기 정보 UPSERT 시 기존 경기가 업데이트된다")
    void upsertGames_updateGames() {
        // given
        GameInfoDto originalGameInfo = new GameInfoDto(
                "TEST_KEY_1",
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
        Game existingGame = Game.create(originalGameInfo);

        GameInfoDto updatedGameInfo = new GameInfoDto(
                "TEST_KEY_1",
                SeriesType.REGULAR_SEASON,
                LocalDate.now(),
                LocalTime.of(18, 30),
                Team.KIA,
                Team.LG,
                3,
                2,
                "TEST",
                "잠실",
                GameStatus.FINISHED,
                null
        );
        List<GameInfoDto> gameInfoDtoList = List.of(updatedGameInfo);

        when(gameRepository.findByGameKeyIn(anyList())).thenReturn(List.of(existingGame));
        when(gameRepository.saveAll(anyList())).thenReturn(null);

        // when
        UpsertGameResponseDto result = gameService.upsertGames(gameInfoDtoList);

        // then
        assertThat(result.getCollectedCount()).isEqualTo(1);
        assertThat(result.getSavedCount()).isZero();
        assertThat(result.getModifiedCount()).isEqualTo(1);
        verify(gameRepository, times(1)).findByGameKeyIn(anyList());
    }

    @Test
    @DisplayName("날짜별 경기 목록 조회 시 정상적으로 조회된다")
    void getGamesByDate() {
        // given
        LocalDate date = LocalDate.now();
        GameInfoDto gameInfoDto = new GameInfoDto(
                "TEST_KEY",
                SeriesType.REGULAR_SEASON,
                date,
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

        when(gameRepository.findByDate(date)).thenReturn(List.of(game));

        // when
        List<GameResponseDto> result = gameService.getGamesByDate(date);

        // then
        assertThat(result).hasSize(1);
        verify(gameRepository, times(1)).findByDate(date);
    }

    @Test
    @DisplayName("경기 상세 조회 시 정상적으로 조회된다")
    void getGameDetail() {
        // given
        Long userId = 1L;
        Long gameId = 1L;

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

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(oddsService.getCurrentOdds(gameId, BetType.HOME_WIN)).thenReturn(2.0);
        when(oddsService.getCurrentOdds(gameId, BetType.AWAY_WIN)).thenReturn(2.0);
        when(oddsService.getCurrentOdds(gameId, BetType.DRAW)).thenReturn(2.0);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:1:game:1:amount")).thenReturn("100");
        when(valueOperations.get("user:1:game:1:type")).thenReturn("HOME_WIN");

        // when
        GameDetailResponseDto result = gameService.getGameDetail(userId, gameId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMyBetType()).isEqualTo(BetType.HOME_WIN);
        assertThat(result.getMyTotalAmount()).isEqualTo(100);
        verify(gameRepository, times(1)).findById(gameId);
        verify(oddsService, times(3)).getCurrentOdds(eq(gameId), any(BetType.class));
    }

    @Test
    @DisplayName("경기 상세 조회 시 경기가 없으면 예외가 발생한다")
    void getGameDetail_notFound() {
        // given
        Long userId = 1L;
        Long gameId = 1L;

        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> gameService.getGameDetail(userId, gameId))
                .isInstanceOf(GameException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.GAME_NOT_FOUND);

        verify(gameRepository, times(1)).findById(gameId);
    }

    @Test
    @DisplayName("경기 상세 조회 시 배팅이 없으면 null을 반환한다")
    void getGameDetail_noBet() {
        // given
        Long userId = 1L;
        Long gameId = 1L;

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

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(oddsService.getCurrentOdds(gameId, BetType.HOME_WIN)).thenReturn(2.0);
        when(oddsService.getCurrentOdds(gameId, BetType.AWAY_WIN)).thenReturn(2.0);
        when(oddsService.getCurrentOdds(gameId, BetType.DRAW)).thenReturn(2.0);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:1:game:1:amount")).thenReturn(null);
        when(valueOperations.get("user:1:game:1:type")).thenReturn(null);

        // when
        GameDetailResponseDto result = gameService.getGameDetail(userId, gameId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMyBetType()).isNull();
        assertThat(result.getMyTotalAmount()).isNull();
    }
}
