package woobl0g.gameservice.bet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeductPointsRequestDto {

    private Long userId;
    private String actionType;
    private Integer amount;

    public static DeductPointsRequestDto of(Long userId, String actionType, Integer amount) {
        return new DeductPointsRequestDto(userId, actionType, amount);
    }
}
