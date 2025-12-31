package woobl0g.pointservice.point.dto;

import lombok.Getter;
import woobl0g.pointservice.point.domain.PointActionType;

@Getter
public class AddPointRequestDto {

    private Long userId;
    private PointActionType actionType;
}
