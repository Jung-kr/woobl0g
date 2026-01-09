package woobl0g.boardservice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeductPointsRequestDto {

    private Long userId;
    private String actionType;

    public static DeductPointsRequestDto of(Long userId, String actionType) {
        return new DeductPointsRequestDto(userId, actionType);
    }
}
