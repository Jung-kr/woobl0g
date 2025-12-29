package woobl0g.boardservice.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
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

    @Schema(
            description = "작성자 사용자 ID (필수)",
            example = "1"
    )
    @NotNull(message = "작성자 ID는 필수 입력값입니다.")
    private Long userId;
}
