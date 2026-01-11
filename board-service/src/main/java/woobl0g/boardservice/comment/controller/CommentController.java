package woobl0g.boardservice.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import woobl0g.boardservice.board.dto.UpdateCommentRequestDto;
import woobl0g.boardservice.comment.dto.CommentResponseDto;
import woobl0g.boardservice.comment.dto.CreateCommentRequestDto;
import woobl0g.boardservice.global.response.ApiResponse;

import java.util.List;

@Tag(name = "Comment [External]", description = "댓글 관련 외부용 API")
public interface CommentController {

    @Operation(
            summary = "댓글 생성",
            description = "게시글에 댓글을 작성합니다. parentId를 포함하면 대댓글로 생성됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "⭕ 댓글 생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 대댓글에는 답글을 달 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 게시글 또는 부모 댓글 또는 사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> create(
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable Long boardId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 생성 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateCommentRequestDto.class)
                    )
            )
            @RequestBody CreateCommentRequestDto dto,
            @Parameter(description = "사용자 ID (헤더)", required = true)
            @RequestHeader("X-User-Id") Long userId
    );

    @Operation(
            summary = "댓글 삭제",
            description = "댓글을 삭제합니다. 작성 후 1일이 지나야 삭제 가능합니다. 답글이 있으면 소프트 삭제됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 댓글 삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 댓글 작성 후 1일이 지나지 않음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "❌ 댓글 삭제 권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 댓글을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "삭제할 댓글 ID", required = true, example = "1")
            @PathVariable Long commentId,
            @Parameter(description = "사용자 ID (헤더)", required = true)
            @RequestHeader("X-User-Id") Long userId
    );

    @Operation(
            summary = "댓글 수정",
            description = "댓글을 수정합니다. 작성 후 1일이 지나야 수정 가능합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 댓글 수정 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 댓글 작성 후 1일이 지나지 않음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "❌ 댓글 수정 권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 댓글을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "수정할 댓글 ID", required = true, example = "1")
            @PathVariable Long commentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 수정 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateCommentRequestDto.class)
                    )
            )
            @RequestBody UpdateCommentRequestDto dto,
            @Parameter(description = "사용자 ID (헤더)", required = true)
            @RequestHeader("X-User-Id") Long userId
    );

    @Operation(
            summary = "댓글 목록 조회",
            description = "특정 게시글의 모든 댓글을 조회합니다. 대댓글은 replies 필드에 포함됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 댓글 목록 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CommentResponseDto.class))
                    )
            )
    })
    ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComments(
            @Parameter(description = "조회할 게시글 ID", required = true, example = "1")
            @PathVariable Long boardId
    );
}
