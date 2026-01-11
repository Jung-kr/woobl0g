package woobl0g.pointservice.point.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import woobl0g.pointservice.point.dto.AddPointRequestDto;
import woobl0g.pointservice.point.dto.DeductPointRequestDto;

@Tag(name = "Point [Internal]", description = "포인트 관련 내부용 API")
public interface PointInternalController {

    @Operation(
            summary = "포인트 적립 (내부용)",
            description = "사용자에게 포인트를 적립합니다. 사용자가 없으면 자동으로 생성됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "⭕ 포인트 적립 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    ResponseEntity<Void> addPoints(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "포인트 적립 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddPointRequestDto.class)
                    )
            )
            @RequestBody AddPointRequestDto addPointRequestDto
    );

    @Operation(
            summary = "포인트 차감 (내부용)",
            description = "사용자의 포인트를 차감합니다. 포인트가 부족하면 실패합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "⭕ 포인트 차감 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 포인트 부족",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 사용자의 포인트 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    ResponseEntity<Void> deductPoints(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "포인트 차감 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DeductPointRequestDto.class)
                    )
            )
            @RequestBody DeductPointRequestDto deductPointRequestDto
    );
}
