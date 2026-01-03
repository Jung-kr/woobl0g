package woobl0g.pointservice.point.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import woobl0g.pointservice.global.response.ApiResponse;
import woobl0g.pointservice.point.dto.PointHistoryResponseDto;

import java.util.List;

@Tag(name = "Point", description = "포인트 관련 외부용 API")
public interface PointController {

    @Operation(
            summary = "포인트 이력 조회",
            description = "특정 사용자의 포인트 변동 이력을 최신순으로 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 포인트를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<List<PointHistoryResponseDto>>> getPointHistory(
            @io.swagger.v3.oas.annotations.Parameter(description = "사용자 ID", required = true)
            Long userId
    );
}
