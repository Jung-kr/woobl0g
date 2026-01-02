package woobl0g.userservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.userservice.user.domain.ActionType;

@Getter
@AllArgsConstructor
public class AddActivityScoreRequestDto {

    private Long userId;
    private ActionType actionType;
}
