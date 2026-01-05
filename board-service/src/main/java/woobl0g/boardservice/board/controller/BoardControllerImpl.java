package woobl0g.boardservice.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.boardservice.board.dto.BoardResponseDto;
import woobl0g.boardservice.board.dto.CreateBoardRequestDto;
import woobl0g.boardservice.board.dto.SearchType;
import woobl0g.boardservice.board.service.BoardService;
import woobl0g.boardservice.global.response.ApiResponse;
import woobl0g.boardservice.global.response.ResponseCode;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardControllerImpl implements BoardController {

    private final BoardService boardService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody CreateBoardRequestDto dto) {
        boardService.create(dto);
        return ResponseEntity
                .status(ResponseCode.BOARD_CREATED.getStatus())
                .body(ApiResponse.success(ResponseCode.BOARD_CREATED));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> getBoard(@PathVariable Long boardId) {
        return ResponseEntity
                .status(ResponseCode.BOARD_GET_SUCCESS.getStatus())
//                .body(ApiResponse.success(ResponseCode.BOARD_GET_SUCCESS, boardService.getBoard(boardId)));
                .body(ApiResponse.success(ResponseCode.BOARD_GET_SUCCESS, boardService.getBoard2(boardId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponseDto>>> getBoards(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) SearchType searchType,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity
                .status(ResponseCode.BOARD_GET_SUCCESS.getStatus())
//                .body(ApiResponse.success(ResponseCode.BOARD_GET_SUCCESS, boardService.getBoards()));
                .body(ApiResponse.success(ResponseCode.BOARD_GET_SUCCESS, boardService.getBoards2(keyword, searchType, pageable)));
    }
}
