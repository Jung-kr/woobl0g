package woobl0g.boardservice.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import woobl0g.boardservice.board.dto.CreateBoardRequestDto;
import woobl0g.boardservice.global.response.ApiResponse;

@Tag(name = "Board", description = "게시글 관련 API")
public interface BoardController {

    @Operation(
            summary = "게시글 작성",
            description = "제목, 내용, userId를 입력받아 게시글 작성을 수행합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "⭕ 게시글 작성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 입력값 유효성 검증 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 작성 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateBoardRequestDto.class)
                    )
            )
            @Valid @RequestBody CreateBoardRequestDto dto
    );
}
