package woobl0g.gameservice.bet.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import woobl0g.gameservice.bet.client.PointClient;
import woobl0g.gameservice.bet.domain.Bet;
import woobl0g.gameservice.bet.domain.BetAction;
import woobl0g.gameservice.bet.domain.BetType;
import woobl0g.gameservice.bet.dto.BetResponseDto;
import woobl0g.gameservice.bet.repository.BetRepository;
import woobl0g.gameservice.game.domain.Game;
import woobl0g.gameservice.game.repository.GameRepository;
import woobl0g.gameservice.global.exception.BetException;
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

@DisplayName("BetService 테스트")
@ExtendWith(MockitoExtension.class)
class BetServiceTest {

    @InjectMocks
    private BetService betService;

    @Mock
    private OddsService oddsService;

    @Mock
    private PointClient pointClient;

    @Mock
    private BetRepository betRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    @DisplayName("사용자 배팅 내역 조회 시 정상적으로 조회된다")
    void getBets() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

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
        Bet bet = Bet.create(userId, game, BetType.HOME_WIN, -100, BetAction.BET);

        when(betRepository.findByUserId(userId, pageable)).thenReturn(List.of(bet));

        // when
        List<BetResponseDto> result = betService.getBets(userId, pageable);

        // then
        assertThat(result).hasSize(1);
        verify(betRepository, times(1)).findByUserId(userId, pageable);
    }
}
