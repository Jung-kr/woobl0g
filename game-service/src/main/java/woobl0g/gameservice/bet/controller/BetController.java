package woobl0g.gameservice.bet.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import woobl0g.gameservice.bet.dto.BetResponseDto;
import woobl0g.gameservice.bet.dto.PlaceBetRequestDto;
import woobl0g.gameservice.global.response.ApiResponse;

import java.util.List;

@Tag(name = "Bet [External]", description = "배팅 관련 외부용 API")
public interface BetController {

    @Operation(
            summary = "배팅하기",
            description = "경기에 배팅합니다. 배팅은 매일 낮 12시부터 경기 시작 30분 전까지 가능합니다. 배팅 금액은 100원 단위로 100원 이상 1,000원 이하만 가능합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 배팅 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 배팅 금액이 유효하지 않거나, 배팅 시간이 아니거나, 포인트가 부족하거나, 다른 결과에 이미 배팅한 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
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
    ResponseEntity<ApiResponse<Void>> placeBet(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "배팅 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PlaceBetRequestDto.class)
                    )
            )
            @RequestBody PlaceBetRequestDto placeBetRequestDto,
            @Parameter(description = "배팅할 경기 ID", required = true, example = "1")
            @PathVariable Long gameId,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") Long userId
    );

    @Operation(
            summary = "배팅 취소",
            description = "경기에 대한 배팅을 취소합니다. 경기 시작 30분 전까지만 취소 가능합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 배팅 취소 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 배팅 시간이 마감되었거나, 이미 정산 또는 취소된 배팅",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "❌ 본인의 배팅만 취소 가능",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 배팅을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> cancelBet(
            @Parameter(description = "취소할 경기 ID", required = true, example = "1")
            @PathVariable Long gameId,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") Long userId
    );

    @Operation(
            summary = "배팅 내역 조회",
            description = "사용자의 배팅 내역을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 배팅 내역 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = BetResponseDto.class))
                    )
            )
    })
    ResponseEntity<ApiResponse<List<BetResponseDto>>> getBets(
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(
                    description = "페이징 정보 (page, size, sort)",
                    required = false
            )
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );
}
