package woobl0g.userservice.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionType {

    BOARD_CREATE(10),
    COMMENT_CREATE(5);

    private final int activityScore;
}
