package woobl0g.userservice.user.dto;

import lombok.Getter;
import woobl0g.userservice.user.domain.ActionType;

@Getter
public class AddActivityScoreRequestDto {

    private Long userId;
    private ActionType actionType;
}
