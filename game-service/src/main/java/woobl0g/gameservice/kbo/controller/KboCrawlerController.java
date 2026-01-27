package woobl0g.gameservice.kbo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.gameservice.game.dto.UpsertGameResponseDto;
import woobl0g.gameservice.game.service.GameService;
import woobl0g.gameservice.global.response.ApiResponse;
import woobl0g.gameservice.global.response.ResponseCode;
import woobl0g.gameservice.game.domain.Game;
import woobl0g.gameservice.kbo.dto.CrawlFullSeasonRequestDto;
import woobl0g.gameservice.kbo.dto.CrawlScheduleRequestDto;
import woobl0g.gameservice.kbo.dto.GameInfoDto;
import woobl0g.gameservice.kbo.service.KboCrawlerService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/kbo")
@RequiredArgsConstructor
public class KboCrawlerController {

    private final GameService gameService;
    private final KboCrawlerService kboCrawlerService;

    @PostMapping("/crawl")
    public ResponseEntity<ApiResponse<UpsertGameResponseDto>> crawlSchedule(@RequestBody CrawlScheduleRequestDto dto) {
        List<GameInfoDto> crawledGames = kboCrawlerService.crawlSchedule(dto.getSeason(), dto.getMonth(), dto.getSeriesType());

        return ResponseEntity
                .status(ResponseCode.KBO_CRAWL_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.KBO_CRAWL_SUCCESS, gameService.upsertGames(crawledGames)));

    }

    @PostMapping("/crawl/full")
    public ResponseEntity<ApiResponse<UpsertGameResponseDto>> crawlFullSeason(@RequestBody CrawlFullSeasonRequestDto dto) {
        List<GameInfoDto> crawledGames = kboCrawlerService.crawlFullSeason(dto.getSeason(), dto.getSeriesType());

        return ResponseEntity
                .status(ResponseCode.KBO_CRAWL_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.KBO_CRAWL_SUCCESS, gameService.upsertGames(crawledGames)));
    }
}
