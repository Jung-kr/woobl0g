package woobl0g.gameservice.bet.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import woobl0g.gameservice.bet.domain.BetType;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("OddsService 테스트")
@ExtendWith(MockitoExtension.class)
class OddsServiceTest {

    @InjectMocks
    private OddsService oddsService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("배팅 풀 업데이트 시 정상적으로 업데이트된다")
    void updateBettingPool() {
        // given
        Long gameId = 1L;
        BetType betType = BetType.HOME_WIN;
        Integer betAmount = 100;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("game:1:pool:HOME_WIN")).thenReturn("0");
        doNothing().when(valueOperations).set(anyString(), anyString());

        // when
        oddsService.updateBettingPool(gameId, betType, betAmount);

        // then
        verify(valueOperations, times(1)).get("game:1:pool:HOME_WIN");
        verify(valueOperations, times(1)).set("game:1:pool:HOME_WIN", "100");
    }

    @Test
    @DisplayName("배팅 풀이 없을 때 배당률은 2.0이다")
    void getCurrentOdds_noPool() {
        // given
        Long gameId = 1L;
        BetType betType = BetType.HOME_WIN;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        // when
        Double odds = oddsService.getCurrentOdds(gameId, betType);

        // then
        assertThat(odds).isEqualTo(2.0);
    }

    @Test
    @DisplayName("배당률 계산 시 정상적으로 계산된다")
    void getCurrentOdds() {
        // given
        Long gameId = 1L;
        BetType betType = BetType.HOME_WIN;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("game:1:pool:HOME_WIN")).thenReturn("300");
        when(valueOperations.get("game:1:pool:AWAY_WIN")).thenReturn("600");
        when(valueOperations.get("game:1:pool:DRAW")).thenReturn("100");

        // when
        Double odds = oddsService.getCurrentOdds(gameId, betType);

        // then
        // (300 + 600 + 100) / 300 = 3.33
        assertThat(odds).isEqualTo(3.33);
    }

    @Test
    @DisplayName("배당률 계산 시 배팅이 없으면 1.0을 반환한다")
    void getCurrentOdds_noTargetPool() {
        // given
        Long gameId = 1L;
        BetType betType = BetType.HOME_WIN;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("game:1:pool:HOME_WIN")).thenReturn("0");
        when(valueOperations.get("game:1:pool:AWAY_WIN")).thenReturn("600");
        when(valueOperations.get("game:1:pool:DRAW")).thenReturn("100");

        // when
        Double odds = oddsService.getCurrentOdds(gameId, betType);

        // then
        assertThat(odds).isEqualTo(1.0);
    }

    @Test
    @DisplayName("배팅 풀 삭제 시 정상적으로 삭제된다")
    void deleteBettingPool() {
        // given
        Long gameId = 1L;

        when(redisTemplate.delete("game:1:pool:HOME_WIN")).thenReturn(true);
        when(redisTemplate.delete("game:1:pool:AWAY_WIN")).thenReturn(true);
        when(redisTemplate.delete("game:1:pool:DRAW")).thenReturn(true);

        // when
        oddsService.deleteBettingPool(gameId);

        // then
        verify(redisTemplate, times(1)).delete("game:1:pool:HOME_WIN");
        verify(redisTemplate, times(1)).delete("game:1:pool:AWAY_WIN");
        verify(redisTemplate, times(1)).delete("game:1:pool:DRAW");
    }
}
