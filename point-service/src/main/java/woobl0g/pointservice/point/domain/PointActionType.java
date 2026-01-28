package woobl0g.pointservice.point.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointActionType {

    SIGN_UP(100),
    BOARD_CREATE(10),
    COMMENT_CREATE(5),
    COMMENT_DELETE(5),
    BOARD_DELETE(10),

    // 배팅 관련 (금액은 0, 실제 금액은 별도 필드)
    BET(0),
    BET_CANCEL(0),
    BET_WIN(0),
    BET_LOSE(0),
    BET_REFUND(0);

    private final int amount;
}
