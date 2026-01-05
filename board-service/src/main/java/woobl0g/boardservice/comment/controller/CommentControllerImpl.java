package woobl0g.boardservice.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.boardservice.comment.dto.CreateCommentRequestDto;
import woobl0g.boardservice.comment.service.CommentService;
import woobl0g.boardservice.global.response.ApiResponse;
import woobl0g.boardservice.global.response.ResponseCode;

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
}
