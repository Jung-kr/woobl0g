package woobl0g.boardservice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddActivityScoreRequestDto {

    private Long userId;
    private String actionType;
}
