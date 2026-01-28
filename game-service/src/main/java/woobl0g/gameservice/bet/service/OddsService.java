package woobl0g.gameservice.bet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import woobl0g.gameservice.bet.domain.BetType;

@Slf4j
@Service
@RequiredArgsConstructor
public class OddsService {

    private final RedisTemplate<String, String> redisTemplate;

    public void updateBettingPool(Long gameId, BetType betType, Integer betAmount) {

        // 1. Redis에서 해당 경기의 배팅 풀 조회
        // Key: game:456:pool:HOME_WIN,
        //      game:456:pool:AWAY_WIN,
        //      game:456:pool:DRAW
        String poolKey = "game:" + gameId + ":pool:" + betType.name();
        String poolStr = redisTemplate.opsForValue().get(poolKey);
        Integer currentPool = (poolStr != null) ? Integer.parseInt(poolStr) : 0;

        // 2. 배팅 풀 업데이트 (배팅 생성 시 +, 취소 시 -)
        Integer newPool = currentPool + betAmount;
        redisTemplate.opsForValue().set(poolKey, String.valueOf(newPool));
    }

    public Double getCurrentOdds(Long gameId, BetType betType) {

        // 1. 각 배팅 풀 조회
        Integer homeWinPool = getPool(gameId, BetType.HOME_WIN);
        Integer awayWinPool = getPool(gameId, BetType.AWAY_WIN);
        Integer drawPool = getPool(gameId, BetType.DRAW);
        Integer totalPool = homeWinPool + awayWinPool + drawPool;

        // 2. 배팅이 없으면 기본 배당률
        if (totalPool == 0) {
            return 2.0;
        }

        // 3. 해당 타입의 배팅 풀
        Integer targetPool = switch (betType) {
            case HOME_WIN -> homeWinPool;
            case AWAY_WIN -> awayWinPool;
            case DRAW -> drawPool;
        };

        // 4. 배당률 계산: 전체 배팅액 / 해당 옵션 배팅액
        if (targetPool == 0) {
            return 1.0; // 배팅이 없으면 최소 배당률
        }

        double odds = (double) totalPool / targetPool;
        return Math.round(odds * 100) / 100.0; // 소수점 2자리
    }

    public void deleteBettingPool(Long gameId) {
        redisTemplate.delete("game:" + gameId + ":pool:HOME_WIN");
        redisTemplate.delete("game:" + gameId + ":pool:AWAY_WIN");
        redisTemplate.delete("game:" + gameId + ":pool:DRAW");
    }

    private Integer getPool(Long gameId, BetType betType) {
        String poolKey = "game:" + gameId + ":pool:" + betType.name();
        String poolStr = redisTemplate.opsForValue().get(poolKey);
        return (poolStr != null) ? Integer.parseInt(poolStr) : 0;
    }
}
