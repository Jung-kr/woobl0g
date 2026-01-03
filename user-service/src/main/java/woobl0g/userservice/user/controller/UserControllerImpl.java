package woobl0g.userservice.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.userservice.global.response.ApiResponse;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.dto.ActivityScoreHistoryResponseDto;
import woobl0g.userservice.user.dto.SignUpRequestDto;
import woobl0g.userservice.user.dto.UserRankingResponseDto;
import woobl0g.userservice.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpRequestDto dto) {
        userService.signUp(dto);
        return ResponseEntity
                .status(ResponseCode.SIGN_UP_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.SIGN_UP_SUCCESS));
    }

    @Override
    @GetMapping("/{userId}/activity-score-history")
    public ResponseEntity<ApiResponse<List<ActivityScoreHistoryResponseDto>>> getActivityScoreHistory(@PathVariable Long userId) {
        return ResponseEntity
                .status(ResponseCode.ACTIVITY_SCORE_HISTORY_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.ACTIVITY_SCORE_HISTORY_GET_SUCCESS, userService.getActivityScoreHistory(userId)));

    }

    @Override
    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<List<UserRankingResponseDto>>> getTopRanking() {
        return ResponseEntity
                .status(ResponseCode.RANKING_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.RANKING_GET_SUCCESS, userService.getTopRanking()));
    }
}
