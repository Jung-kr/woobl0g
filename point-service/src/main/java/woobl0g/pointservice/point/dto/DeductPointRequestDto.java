package woobl0g.pointservice.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.pointservice.point.domain.PointActionType;

@Getter
@AllArgsConstructor
public class DeductPointRequestDto {

    private Long userId;
    private PointActionType actionType;

    public static DeductPointRequestDto of(Long userId, PointActionType actionType) {
        return new DeductPointRequestDto(userId, actionType);
    }
}
