package woobl0g.boardservice.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.boardservice.comment.dto.CommentResponseDto;
import woobl0g.boardservice.comment.dto.CreateCommentRequestDto;
import woobl0g.boardservice.comment.service.CommentService;
import woobl0g.boardservice.global.response.ApiResponse;
import woobl0g.boardservice.global.response.ResponseCode;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/comments")
public class CommentControllerImpl {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@PathVariable Long boardId, @RequestBody CreateCommentRequestDto dto) {
        commentService.create(boardId, dto, 2L);
        return ResponseEntity
                .status(ResponseCode.COMMENT_CREATED.getStatus())
                .body(ApiResponse.success(ResponseCode.COMMENT_CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComments(@PathVariable Long boardId) {
        return ResponseEntity
                .status(ResponseCode.COMMENT_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.COMMENT_GET_SUCCESS, commentService.getComments(boardId)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long commentId) {
        commentService.delete(commentId, 2L);
        return ResponseEntity
                .status(ResponseCode.COMMENT_DELETE_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.COMMENT_DELETE_SUCCESS));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long commentId,
            @RequestBody CreateCommentRequestDto dto) {
        commentService.update(commentId, dto, 2L);
        return ResponseEntity
                .status(ResponseCode.COMMENT_UPDATE_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.COMMENT_UPDATE_SUCCESS));
    }
}
