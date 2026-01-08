package woobl0g.pointservice.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.pointservice.global.response.ApiResponse;
import woobl0g.pointservice.global.response.ResponseCode;
import woobl0g.pointservice.point.dto.PointHistoryResponseDto;
import woobl0g.pointservice.point.dto.PointRankingResponseDto;
import woobl0g.pointservice.point.service.PointService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointControllerImpl implements PointController {

    private final PointService pointService;

    @Override
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PointHistoryResponseDto>>> getPointHistory(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity
                .status(ResponseCode.POINT_HISTORY_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.POINT_HISTORY_GET_SUCCESS, pointService.getPointHistory(userId)));
    }

    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<List<PointRankingResponseDto>>> getPointRanking(
            @PageableDefault(page = 0, size = 10, sort = "amount", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity
                .status(ResponseCode.POINT_RANKING_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.POINT_RANKING_GET_SUCCESS, pointService.getPointRanking(pageable)));
    }
}
