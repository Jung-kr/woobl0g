package woobl0g.pointservice.point.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointActionType {

    SIGN_UP,
    BOARD_CREATE,
    COMMENT_CREATE,
    COMMENT_DELETE,
    BOARD_DELETE,

    // 배팅 관련
    BET,
    BET_CANCEL,
    BET_WIN,
    BET_LOSE,
    BET_REFUND;
}
