package woobl0g.boardservice.comment.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.boardservice.board.dto.UpdateCommentRequestDto;
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
    public ResponseEntity<ApiResponse<Void>> create(
            @PathVariable Long boardId,
            @RequestBody CreateCommentRequestDto dto,
            @RequestHeader("X-User-Id") Long userId) {
        commentService.create(boardId, dto, userId);
        return ResponseEntity
                .status(ResponseCode.COMMENT_CREATED.getStatus())
                .body(ApiResponse.success(ResponseCode.COMMENT_CREATED));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long commentId,
            @RequestHeader("X-User-Id") Long userId) {
        commentService.delete(commentId, userId);
        return ResponseEntity
                .status(ResponseCode.COMMENT_DELETE_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.COMMENT_DELETE_SUCCESS));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequestDto dto,
            @RequestHeader("X-User-Id") Long userId) {
        commentService.update(commentId, dto, userId);
        return ResponseEntity
                .status(ResponseCode.COMMENT_UPDATE_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.COMMENT_UPDATE_SUCCESS));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComments(@PathVariable Long boardId) {
        return ResponseEntity
                .status(ResponseCode.COMMENT_GET_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.COMMENT_GET_SUCCESS, commentService.getComments(boardId)));
    }
}
