package woobl0g.gameservice.bet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.gameservice.bet.dto.BetResponseDto;
import woobl0g.gameservice.bet.dto.PlaceBetRequestDto;
import woobl0g.gameservice.bet.service.BetService;
import woobl0g.gameservice.global.response.ApiResponse;
import woobl0g.gameservice.global.response.ResponseCode;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bets")
public class BetControllerImpl implements BetController {

    private final BetService betService;

    @Override
    @PostMapping("/games/{gameId}")
    public ResponseEntity<ApiResponse<Void>> placeBet(
            @RequestBody PlaceBetRequestDto placeBetRequestDto,
            @PathVariable Long gameId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        betService.placeBet(userId, placeBetRequestDto, gameId);
        return ResponseEntity
                .status(ResponseCode.BET_PLACED_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.BET_PLACED_SUCCESS));
    }

    @Override
    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<ApiResponse<Void>> cancelBet(
            @PathVariable Long gameId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        betService.cancelBet(userId, gameId);
        return ResponseEntity
                .status(ResponseCode.BET_CANCELLED_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.BET_CANCELLED_SUCCESS));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<BetResponseDto>>> getBets(
            @RequestHeader("X-User-Id") Long userId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity
                .status(ResponseCode.BET_LIST_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.BET_LIST_GET_SUCCESS, betService.getBets(userId, pageable)));
    }
}
