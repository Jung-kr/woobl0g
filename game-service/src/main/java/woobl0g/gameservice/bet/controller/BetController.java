package woobl0g.gameservice.bet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.gameservice.bet.dto.BetResponseDto;
import woobl0g.gameservice.bet.dto.CancelBetRequestDto;
import woobl0g.gameservice.bet.dto.PlaceBetRequestDto;
import woobl0g.gameservice.bet.service.BetService;
import woobl0g.gameservice.global.response.ApiResponse;
import woobl0g.gameservice.global.response.ResponseCode;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bets")
public class BetController {

    private final BetService betService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> placeBet(
            @RequestBody PlaceBetRequestDto placeBetRequestDto,
            @RequestHeader("X-User-Id") Long userId
    ) {
        betService.placeBet(userId, placeBetRequestDto);
        return ResponseEntity
                .status(ResponseCode.BET_PLACED_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.BET_PLACED_SUCCESS));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> cancelBet(
            @RequestBody CancelBetRequestDto cancelBetRequestDto,
            @RequestHeader("X-User-Id") Long userId
    ) {
        betService.cancelBet(userId, cancelBetRequestDto);
        return ResponseEntity
                .status(ResponseCode.BET_CANCELLED_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.BET_CANCELLED_SUCCESS));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BetResponseDto>>> getBets(
            @RequestHeader("X-User-Id") Long userId,
            @PageableDefault(page = 0, size = 10, sort = "betDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity
                .status(ResponseCode.BET_LIST_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.BET_LIST_GET_SUCCESS, betService.getBets(userId, pageable)));
    }
}
