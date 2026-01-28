package woobl0g.gameservice.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.gameservice.bet.domain.BetType;
import woobl0g.gameservice.bet.service.OddsService;
import woobl0g.gameservice.game.domain.Game;
import woobl0g.gameservice.game.dto.GameDetailResponseDto;
import woobl0g.gameservice.game.dto.GameResponseDto;
import woobl0g.gameservice.game.dto.UpsertGameResponseDto;
import woobl0g.gameservice.game.repository.GameRepository;
import woobl0g.gameservice.global.exception.GameException;
import woobl0g.gameservice.global.response.ResponseCode;
import woobl0g.gameservice.kbo.dto.GameInfoDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final OddsService oddsService;
    private final GameRepository gameRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public UpsertGameResponseDto upsertGames(List<GameInfoDto> gameInfoDtoList) {
        log.info("게임 정보 UPSERT 시작 - 수집된 경기 수: {}", gameInfoDtoList.size());

        // 1. gameKey 리스트 추출
        List<String> gameKeys = gameInfoDtoList.stream()
                .map(GameInfoDto::getGameKey)
                .toList();

        // 2. 기존 게임들을 gameKey로 일괄 조회 후 Map으로 변환
        Map<String, Game> existingGamesMap = gameRepository.findByGameKeyIn(gameKeys)
                .stream()
                .collect(Collectors.toMap(Game::getGameKey, game -> game));

        // 3. 새 경기 vs 업데이트할 경기 분류 (partition 사용)
        Map<Boolean, List<GameInfoDto>> partitioned = gameInfoDtoList.stream()
                .collect(Collectors.partitioningBy(
                        gameInfoDto -> existingGamesMap.get(gameInfoDto.getGameKey()) == null
                ));

        List<GameInfoDto> newGameInfos = partitioned.get(true);    // 새 경기
        List<GameInfoDto> toUpdateGameInfos = partitioned.get(false);    // 업데이트할 경기

        // 4. 새 경기 저장
        List<Game> newGames = newGameInfos.stream()
                .map(Game::create)
                .toList();
        gameRepository.saveAll(newGames);

        // 5. 기존 경기 업데이트 (실제 변경 발생 시에만 카운트)
        int modifiedCount = 0;
        for (GameInfoDto gameInfoDto : toUpdateGameInfos) {
            Game existingGame = existingGamesMap.get(gameInfoDto.getGameKey());
            if (existingGame != null && existingGame.update(gameInfoDto)) {
                modifiedCount++;  // update()가 true 반환 시에만 증가
            }
        }

        log.info("게임 정보 UPSERT 완료 - 총: {}, 신규: {}, 수정: {}", gameInfoDtoList.size(), newGames.size(), modifiedCount);
        return new UpsertGameResponseDto(gameInfoDtoList.size(), newGames.size(), modifiedCount);
    }

    @Transactional(readOnly = true)
    public List<GameResponseDto> getGamesByDate(LocalDate date) {
        List<Game> games = gameRepository.findByDate(date);

        return games.stream()
                .map(game -> {
                    boolean isBettingOpen = !game.isBettingClosed() && LocalDateTime.now().isAfter(game.getBettingOpenAt());
                    return GameResponseDto.from(game, isBettingOpen);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public GameDetailResponseDto getGameDetail(Long userId, Long gameId) {
        // 1. 경기 조회
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(ResponseCode.GAME_NOT_FOUND));

        // 2. 배당률 계산
        Double homeWinOdds = oddsService.getCurrentOdds(gameId, BetType.HOME_WIN);
        Double awayWinOdds = oddsService.getCurrentOdds(gameId, BetType.AWAY_WIN);
        Double drawOdds = oddsService.getCurrentOdds(gameId, BetType.DRAW);

        // 3. 내 배팅 정보 조회 (Redis)
        String amountKey = "user:" + userId + ":game:" + gameId + ":amount";
        String typeKey = "user:" + userId + ":game:" + gameId + ":type";

        String amountStr = redisTemplate.opsForValue().get(amountKey);
        String typeStr = redisTemplate.opsForValue().get(typeKey);

        BetType myBetType = null;
        Integer myTotalAmount = null;
        if (amountStr != null && typeStr != null) {
            myBetType = BetType.valueOf(typeStr);
            myTotalAmount = Integer.parseInt(amountStr);
        }

        return GameDetailResponseDto.of(game, homeWinOdds, awayWinOdds, drawOdds, myBetType, myTotalAmount);
    }
}
