package woobl0g.pointservice.point.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import woobl0g.pointservice.global.response.ApiResponse;
import woobl0g.pointservice.point.dto.PageResponse;
import woobl0g.pointservice.point.dto.PointFailureHistoryResponseDto;

@Tag(name = "Point [Admin]", description = "포인트 관리자용 API")
public interface PointAdminController {

    @Operation(
            summary = "[관리자] 포인트 적립 실패 내역 조회",
            description = "포인트 적립에 실패한 내역을 페이징하여 조회합니다. PENDING 상태만 조회됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 포인트 적립 실패 내역 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<PointFailureHistoryResponseDto>>> getFailureHistories(
            @Parameter(
                    description = "페이징 정보 (page, size, sort)",
                    required = false
            )
            @PageableDefault(page = 0, size = 10, sort = "failedAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(
            summary = "[관리자] 포인트 적립 실패 재시도",
            description = "포인트 적립 실패 건에 대해 재시도를 수행합니다. PENDING 상태만 재시도 가능합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 포인트 적립 재시도 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 이미 처리된 포인트 적립 실패 내역 | 포인트 부족",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 포인트 적립 실패 내역을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> retryFailedPoint(
            @Parameter(description = "재시도할 실패 내역 ID", required = true, example = "1")
            @PathVariable Long failureId
    );

    @Operation(
            summary = "[관리자] 포인트 적립 실패 무시",
            description = "포인트 적립 실패 건을 무시 처리합니다. 상태가 IGNORED로 변경됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 포인트 적립 실패 무시 처리 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 포인트 적립 실패 내역을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> ignoreFailedPoint(
            @Parameter(description = "무시 처리할 실패 내역 ID", required = true, example = "1")
            @PathVariable Long failureId
    );
}
