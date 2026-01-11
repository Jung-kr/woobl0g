package woobl0g.pointservice.point.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import woobl0g.pointservice.global.response.ApiResponse;
import woobl0g.pointservice.point.dto.PageResponse;
import woobl0g.pointservice.point.dto.PointHistoryResponseDto;
import woobl0g.pointservice.point.dto.PointRankingResponseDto;

import java.util.List;

@Tag(name = "Point [External]", description = "포인트 관련 외부용 API")
public interface PointController {

    @Operation(
            summary = "포인트 이력 조회",
            description = "사용자의 포인트 적립/차감 이력을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 포인트 이력 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 사용자의 포인트 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<PointHistoryResponseDto>>> getPointHistory(
            @Parameter(
                    description = "페이징 정보 (page, size, sort)",
                    required = false
            )
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "사용자 ID (헤더)", required = true)
            @RequestHeader("X-User-Id") Long userId
    );

    @Operation(
            summary = "포인트 랭킹 조회",
            description = "전체 사용자의 포인트 랭킹을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 포인트 랭킹 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PointRankingResponseDto.class))
                    )
            )
    })
    ResponseEntity<ApiResponse<List<PointRankingResponseDto>>> getPointRanking(
            @Parameter(
                    description = "페이징 정보 (page, size, sort)",
                    required = false
            )
            @PageableDefault(page = 0, size = 10, sort = "amount", direction = Sort.Direction.DESC) Pageable pageable
    );
}
