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


}
