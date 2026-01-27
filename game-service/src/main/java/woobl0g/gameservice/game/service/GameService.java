package woobl0g.gameservice.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.gameservice.game.domain.Game;
import woobl0g.gameservice.game.dto.UpsertGameResponseDto;
import woobl0g.gameservice.game.repository.GameRepository;
import woobl0g.gameservice.kbo.dto.GameInfoDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

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
}
