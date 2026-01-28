package woobl0g.pointservice.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.pointservice.point.domain.PointActionType;

@Getter
@AllArgsConstructor
public class AddPointRequestDto {

    private Long userId;
    private PointActionType actionType;
    private Integer amount;

    public static AddPointRequestDto of(Long userId, PointActionType actionType, Integer amount) {
        return new AddPointRequestDto(userId, actionType, amount);
    }
}
