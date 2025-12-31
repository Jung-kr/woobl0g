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
    BOARD_DELETE(10);

    private final int amount;
}
