package woobl0g.pointservice.point.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.pointservice.global.response.ApiResponse;
import woobl0g.pointservice.global.response.ResponseCode;
import woobl0g.pointservice.point.dto.PointFailureHistoryResponseDto;
import woobl0g.pointservice.point.service.PointAdminService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/points")
public class PointAdminController {

    private final PointAdminService pointAdminService;

    @GetMapping("/failures")
    public ResponseEntity<ApiResponse<List<PointFailureHistoryResponseDto>>> getFailureHistories(
            @PageableDefault(page = 0, size = 10, sort = "failedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity
                .status(ResponseCode.ADMIN_POINT_FAILURE_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.ADMIN_POINT_FAILURE_GET_SUCCESS, pointAdminService.getFailureHistories(pageable)));
    }

    @PostMapping("/failures/{failureId}/retry")
    public ResponseEntity<ApiResponse<Void>> retryFailedPoint(@PathVariable Long failureId) {
        pointAdminService.retryFailedPoint(failureId);
        return ResponseEntity
                .status(ResponseCode.ADMIN_POINT_FAILURE_RETRY_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.ADMIN_POINT_FAILURE_RETRY_SUCCESS));
    }

    @PostMapping("/failures/{failureId}/ignore")
    public ResponseEntity<ApiResponse<Void>> ignoreFailedPoint(@PathVariable Long failureId) {
        pointAdminService.ignoreFailedPoint(failureId);
        return ResponseEntity
                .status(ResponseCode.ADMIN_POINT_FAILURE_IGNORE_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.ADMIN_POINT_FAILURE_IGNORE_SUCCESS));
    }
}