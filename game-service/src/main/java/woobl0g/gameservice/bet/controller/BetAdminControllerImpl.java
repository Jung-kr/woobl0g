package woobl0g.gameservice.bet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.gameservice.bet.service.BetService;
import woobl0g.gameservice.global.response.ApiResponse;
import woobl0g.gameservice.global.response.ResponseCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/bets")
public class BetAdminControllerImpl implements BetAdminController {

    private final BetService betService;

    @Override
    @PostMapping("/games/{gameId}/settlements")
    public ResponseEntity<ApiResponse<Void>> settleBets(
            @PathVariable Long gameId
    ) {
        betService.settleBets(gameId);
        return ResponseEntity
                .status(ResponseCode.BET_SETTLEMENT_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.BET_SETTLEMENT_SUCCESS));
    }
}
