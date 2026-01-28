package woobl0g.gameservice.bet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.gameservice.bet.client.PointClient;
import woobl0g.gameservice.bet.domain.Bet;
import woobl0g.gameservice.bet.domain.BetAction;
import woobl0g.gameservice.bet.domain.BetType;
import woobl0g.gameservice.bet.dto.BetResponseDto;
import woobl0g.gameservice.bet.dto.CancelBetRequestDto;
import woobl0g.gameservice.bet.dto.PlaceBetRequestDto;
import woobl0g.gameservice.bet.repository.BetRepository;
import woobl0g.gameservice.game.domain.Game;
import woobl0g.gameservice.game.repository.GameRepository;
import woobl0g.gameservice.global.exception.BetException;
import woobl0g.gameservice.global.exception.GameException;
import woobl0g.gameservice.global.response.ResponseCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BetService {

    private final OddsService oddsService;
    private final PointClient pointClient;
    private final BetRepository betRepository;
    private final GameRepository gameRepository;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 배팅 생성 -> 보상 트랜잭션 & 포인트 차감 시 actionType 처리
     */
    @Transactional
    public void placeBet(Long userId, PlaceBetRequestDto dto) {
        Long gameId = dto.getGameId();
        BetType betType = dto.getBetType();
        Integer betAmount = dto.getBetAmount();

        boolean isPointDeducted = false;

        try {
            // 0. Redis에서 해당 경기에 이미 배팅한 금액 조회
            // Key: user:123:game:456:amount
            String amountKey = "user:" + userId + ":game:" + gameId + ":amount";
            String amountStr = redisTemplate.opsForValue().get(amountKey);
            Integer totalBetAmount = (amountStr != null) ? Integer.parseInt(amountStr) : 0;

            // 1. 배팅 금액 검증
            if (betAmount % 100 != 0 || betAmount < 100 || totalBetAmount + betAmount > 1000) {
                throw new BetException(ResponseCode.INVALID_BET_AMOUNT);
            }

            // 2. 경기 조회 및 배팅 가능 시간 검증
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new GameException(ResponseCode.GAME_NOT_FOUND));
            if (LocalDateTime.now().isBefore(game.getBettingOpenAt())) {
                throw new GameException(ResponseCode.BETTING_NOT_OPEN);
            }
            if (game.isBettingClosed()) {
                throw new GameException(ResponseCode.BETTING_CLOSED);
            }

            // 3. 동일 경기 동일 결과 배팅 여부 확인 (추가 배팅은 같은 결과만 가능)
            // Key: user:123:game:456:type
            String typeKey = "user:" + userId + ":game:" + gameId + ":type";
            String existingBetType = redisTemplate.opsForValue().get(typeKey);
            if (existingBetType != null && !existingBetType.equals(betType.name())) {
                throw new BetException(ResponseCode.DIFFERENT_BET_TYPE_NOT_ALLOWED);
            }
            if (existingBetType == null) {
                // 최초 배팅이면 Redis에 배팅 타입 저장
                redisTemplate.opsForValue().set(typeKey, betType.name());
            }

            // 4. 포인트 차감 (Point Service 호출)
            pointClient.deductPoints(userId, null);
            isPointDeducted = true;

            // 5. 배팅 생성 및 저장 (음수로)
            Bet bet = Bet.create(userId, game, betType, -betAmount, BetAction.BET);
            betRepository.save(bet);

            // 6. Redis 누적 금액 업데이트
            redisTemplate.opsForValue().set(amountKey, String.valueOf(totalBetAmount + betAmount));

            // 7. 배팅 풀 업데이트 (배당률 계산을 위한)
            oddsService.updateBettingPool(gameId, betType, betAmount);
        } catch (BetException | GameException e) {
            throw e;
        } catch (Exception e) {
            if(isPointDeducted) {
                pointClient.addPoints(userId, null);
            }
            throw new BetException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 배팅 취소 -> 포인트 환불 시 actionType 처리
     */
    @Transactional
    public void cancelBet(Long userId, CancelBetRequestDto dto) {
        Long gameId = dto.getGameId();

        // 1. Redis에서 현재 활성 배팅 확인
        String amountKey = "user:" + userId + ":game:" + gameId + ":amount";
        String typeKey = "user:" + userId + ":game:" + gameId + ":type";

        String amountStr = redisTemplate.opsForValue().get(amountKey);
        String betTypeStr = redisTemplate.opsForValue().get(typeKey);

        if (amountStr == null || betTypeStr == null) {
            throw new BetException(ResponseCode.BET_NOT_FOUND);
        }

        Integer totalAmount = Integer.parseInt(amountStr);
        BetType betType = BetType.valueOf(betTypeStr);

        // 2. 배팅 마감 여부 검증 -> 마감 시 취소 불가능
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(ResponseCode.GAME_NOT_FOUND));
        if (game.isBettingClosed()) {
            throw new GameException(ResponseCode.BETTING_CLOSED);
        }

        // 3. 취소 기록 INSERT
        Bet cancelBet = Bet.create(userId, game, betType, totalAmount, BetAction.CANCEL);
        betRepository.save(cancelBet);

        // 4. Redis 데이터 삭제
        redisTemplate.delete(amountKey);
        redisTemplate.delete(typeKey);

        // 5. 포인트 환불
        pointClient.addPoints(userId, null);

        // 6. 배팅 풀 업데이트
        oddsService.updateBettingPool(gameId, betType, -totalAmount);
    }

    /**
     * 사용자 배팅 내역 조회
     */
    @Transactional(readOnly = true)
    public List<BetResponseDto> getBets(Long userId, Pageable pageable) {
        List<Bet> bets = betRepository.findByUserId(userId, pageable);

        return bets.stream()
                .map(BetResponseDto::from)
                .toList();
    }

    /**
     * 배팅 정산 (Admin API용) -> 포인트 환불 시 actionType 처리
     */
    @Transactional
    public void settleBets(Long gameId) {
        // 1. 경기 조회
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(ResponseCode.GAME_NOT_FOUND));
        
        // 2. 경기 결과 판정
        BetType winningType = determineWinner(game);

        // 3. Redis에서 해당 경기의 모든 배팅 조회
        // Key: user:123:game:456:amount
        Set<String> amountKeys = redisTemplate.keys("user:*:game:" + gameId + ":amount");
        if (amountKeys == null || amountKeys.isEmpty()) {
            log.info("정산할 배팅이 없습니다: gameId={}", gameId);
            return;
        }

        // 4. 각 사용자별 정산 처리
        for (String amountKey : amountKeys) {
            // Redis 키에서 userId 추출: user:123:game:456:amount -> 123
            String[] parts = amountKey.split(":");
            Long userId = Long.parseLong(parts[1]);

            // Redis에서 배팅 정보 조회
            String typeKey = "user:" + userId + ":game:" + gameId + ":type";
            String amountStr = redisTemplate.opsForValue().get(amountKey);
            String betTypeStr = redisTemplate.opsForValue().get(typeKey);

            if (amountStr == null || betTypeStr == null) {
                log.warn("Redis 데이터 불일치: userId={}, gameId={}", userId, gameId);
                continue;
            }

            Integer totalBetAmount = Integer.parseInt(amountStr);
            BetType userBetType = BetType.valueOf(betTypeStr);

            // 해당 사용자의 배당률
            Double odds = oddsService.getCurrentOdds(gameId, userBetType);

            // 5. 결과에 따라 정산 기록 생성
            if (winningType == null) {
                // 무승부 또는 경기 취소 -> 환불
                Bet refund = Bet.create(userId, game, userBetType, totalBetAmount, BetAction.REFUND);
                betRepository.save(refund);
                pointClient.addPoints(userId, null); // 원금 환불
                log.info("배팅 환불: userId={}, amount={}", userId, totalBetAmount);

            } else if (userBetType == winningType) {
                // 당첨
                int rewardAmount = (int) (totalBetAmount * odds);
                Bet win = Bet.create(userId, game, userBetType, rewardAmount, BetAction.WIN);
                betRepository.save(win);
                pointClient.addPoints(userId, null); // 당첨금 지급
                log.info("배팅 당첨: userId={}, betAmount={}, reward={}, odds={}", userId, totalBetAmount, rewardAmount, odds);

            } else {
                // 낙첨
                Bet lose = Bet.create(userId, game, userBetType, 0, BetAction.LOSE);
                betRepository.save(lose);
                log.info("배팅 낙첨: userId={}, betAmount={}", userId, totalBetAmount);
            }

            // 6. Redis 사용자별 데이터 삭제
            redisTemplate.delete(amountKey);
            redisTemplate.delete(typeKey);
        }

        // 7. Redis 배팅 풀 정리
        oddsService.deleteBettingPool(gameId);

        log.info("배팅 정산 완료: gameId={}, 총 {}명", gameId, amountKeys.size());
    }

    private BetType determineWinner(Game game) {
        if (game.getGameStatus().name().contains("CANCELLED")
                || game.getAwayScore() == null
                || game.getHomeScore() == null) {
            return null; // 환불 처리
        }
        // 무승부
        if (game.getHomeScore().equals(game.getAwayScore())) {
            return BetType.DRAW;
        }
        // 홈팀 승리
        if (game.getHomeScore() > game.getAwayScore()) {
            return BetType.HOME_WIN;
        }
        // 원정팀 승리
        return BetType.AWAY_WIN;
    }
}
