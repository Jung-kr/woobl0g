package woobl0g.boardservice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateBoardRequestDto {

    private String title;
    private String content;
}
