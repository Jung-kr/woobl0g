package woobl0g.gameservice.kbo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import woobl0g.gameservice.game.dto.UpsertGameResponseDto;
import woobl0g.gameservice.global.response.ApiResponse;
import woobl0g.gameservice.kbo.dto.CrawlFullSeasonRequestDto;
import woobl0g.gameservice.kbo.dto.CrawlScheduleRequestDto;

@Tag(name = "KBO Crawler [Admin]", description = "KBO 경기 일정 크롤링 관리 API")
public interface KboCrawlerController {

    @Operation(
            summary = "KBO 경기 일정 크롤링 (월별)",
            description = "KBO 공식 홈페이지에서 특정 시즌의 특정 월 경기 일정을 크롤링합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 크롤링 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpsertGameResponseDto.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "❌ 크롤링 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<UpsertGameResponseDto>> crawlSchedule(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "크롤링 요청 정보 (시즌, 월, 시리즈 타입)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CrawlScheduleRequestDto.class)
                    )
            )
            @RequestBody CrawlScheduleRequestDto dto
    );

    @Operation(
            summary = "KBO 경기 일정 크롤링 (전체 시즌)",
            description = "KBO 공식 홈페이지에서 특정 시즌의 전체 경기 일정을 크롤링합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 크롤링 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpsertGameResponseDto.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "❌ 크롤링 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<UpsertGameResponseDto>> crawlFullSeason(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "크롤링 요청 정보 (시즌, 시리즈 타입)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CrawlFullSeasonRequestDto.class)
                    )
            )
            @RequestBody CrawlFullSeasonRequestDto dto
    );
}
