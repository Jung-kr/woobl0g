package woobl0g.boardservice.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import woobl0g.boardservice.board.dto.*;
import woobl0g.boardservice.global.response.ApiResponse;

@Tag(name = "Board [External]", description = "게시글 관련 외부용 API")
public interface BoardController {

    @Operation(
            summary = "게시글 생성",
            description = "제목과 내용을 입력받아 게시글을 생성합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "⭕ 게시글 생성 성공",
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
                    description = "게시글 생성 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateBoardRequestDto.class)
                    )
            )
            @Valid @RequestBody CreateBoardRequestDto dto,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") Long userId
    );

    @Operation(
            summary = "게시글 삭제",
            description = "게시글 ID를 사용하여 게시글을 삭제합니다. 작성 후 1일이 지나야 삭제 가능합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 게시글 삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 게시글 작성 후 1일이 지나지 않음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "❌ 게시글 삭제 권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 게시글을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "삭제할 게시글 ID", required = true, example = "1")
            @PathVariable Long boardId,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") Long userId
    );

    @Operation(
            summary = "게시글 수정",
            description = "게시글 ID를 사용하여 게시글을 수정합니다. 작성 후 1일이 지나야 수정 가능합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 게시글 수정 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 게시글 작성 후 1일이 지나지 않음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "❌ 게시글 수정 권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 게시글을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "수정할 게시글 ID", required = true, example = "1")
            @PathVariable Long boardId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 수정 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateBoardRequestDto.class)
                    )
            )
            @RequestBody UpdateBoardRequestDto dto,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") Long userId
    );

    @Operation(
            summary = "단일 게시글 조회",
            description = "게시글 ID를 사용하여 게시글 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 게시글 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 게시글을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<BoardResponseDto>> getBoard(
            @Parameter(description = "조회할 게시글 ID", required = true, example = "1")
            @PathVariable Long boardId
    );

    @Operation(
            summary = "게시글 목록 조회 (검색 및 페이징)",
            description = "게시글 목록을 페이징하여 조회합니다. 검색 타입과 키워드로 필터링 가능합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 게시글 목록 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<BoardResponseDto>>> getBoards(
            @Parameter(
                    description = "검색 키워드 (선택)",
                    required = false,
                    example = "제목"
            )
            @RequestParam(required = false) String keyword,
            @Parameter(
                    description = "검색 타입 (TITLE, CONTENT, ALL, EMAIL)",
                    required = false,
                    schema = @Schema(implementation = SearchType.class)
            )
            @RequestParam(required = false) SearchType searchType,
            @Parameter(
                    description = "페이징 정보 (page, size, sort)",
                    required = false
            )
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );
}
