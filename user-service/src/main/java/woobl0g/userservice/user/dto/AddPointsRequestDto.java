package woobl0g.userservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddPointsRequestDto {

    private Long userId;
    private String actionType;
}
