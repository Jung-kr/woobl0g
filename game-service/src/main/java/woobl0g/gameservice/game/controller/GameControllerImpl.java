package woobl0g.gameservice.game.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.gameservice.game.dto.GameDetailResponseDto;
import woobl0g.gameservice.game.dto.GameResponseDto;
import woobl0g.gameservice.game.service.GameService;
import woobl0g.gameservice.global.response.ApiResponse;
import woobl0g.gameservice.global.response.ResponseCode;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameControllerImpl implements GameController {

    private final GameService gameService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<GameResponseDto>>> getGamesByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        return ResponseEntity
                .status(ResponseCode.GAME_LIST_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.GAME_LIST_GET_SUCCESS, gameService.getGamesByDate(date)));
    }

    @Override
    @GetMapping("/{gameId}")
    public ResponseEntity<ApiResponse<GameDetailResponseDto>> getGameDetail(
            @PathVariable Long gameId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity
                .status(ResponseCode.GAME_DETAIL_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.GAME_DETAIL_GET_SUCCESS, gameService.getGameDetail(userId, gameId)));
    }
}
