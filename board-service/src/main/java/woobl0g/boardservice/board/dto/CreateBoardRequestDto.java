package woobl0g.boardservice.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateBoardRequestDto {

    @Schema(
            description = "게시글 제목 (필수)",
            example = "게시글 제목"
    )
    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;

    @Schema(
            description = "게시글 내용 (필수)",
            example = "게시글 내용"
    )
    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;
}
