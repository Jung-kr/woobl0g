package woobl0g.gameservice.game.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import woobl0g.gameservice.game.dto.GameDetailResponseDto;
import woobl0g.gameservice.game.dto.GameResponseDto;
import woobl0g.gameservice.global.response.ApiResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Game [External]", description = "경기 관련 외부용 API")
public interface GameController {

    @Operation(
            summary = "날짜별 경기 목록 조회",
            description = "특정 날짜의 KBO 경기 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 경기 목록 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GameResponseDto.class))
                    )
            )
    })
    ResponseEntity<ApiResponse<List<GameResponseDto>>> getGamesByDate(
            @Parameter(
                    description = "조회할 날짜 (yyyy-MM-dd 형식)",
                    required = true,
                    example = "2024-04-01"
            )
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    );

    @Operation(
            summary = "경기 상세 조회",
            description = "특정 경기의 상세 정보를 조회합니다. 사용자의 배팅 정보도 함께 반환됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 경기 상세 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GameDetailResponseDto.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 경기를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<GameDetailResponseDto>> getGameDetail(
            @Parameter(description = "조회할 경기 ID", required = true, example = "1")
            @PathVariable Long gameId,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") Long userId
    );
}
