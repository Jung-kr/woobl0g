package woobl0g.boardservice.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woobl0g.boardservice.board.dto.CreateBoardRequestDto;
import woobl0g.boardservice.board.service.BoardService;
import woobl0g.boardservice.global.response.ApiResponse;
import woobl0g.boardservice.global.response.ResponseCode;

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
}
