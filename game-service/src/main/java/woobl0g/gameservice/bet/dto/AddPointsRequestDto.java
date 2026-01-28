package woobl0g.gameservice.bet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddPointsRequestDto {

    private Long userId;
    private String actionType;
    private Integer amount;

    public static AddPointsRequestDto of(Long userId, String actionType, Integer amount) {
        return new AddPointsRequestDto(userId, actionType, amount);
    }
}