package woobl0g.boardservice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddPointsRequestDto {

    private Long userId;
    private String actionType;

    public static AddPointsRequestDto of(Long userId, String actionType) {
        return new AddPointsRequestDto(userId, actionType);
    }
}
