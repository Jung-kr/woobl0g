package woobl0g.pointservice.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import woobl0g.pointservice.global.exception.PointException;
import woobl0g.pointservice.global.response.ResponseCode;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Point 도메인 테스트")
class PointTest {

    @Test
    @DisplayName("포인트 생성 시 초기 금액은 0이다")
    void create() {
        //given
        Long userId = 1L;

        //when
        Point point = Point.create(userId);

        //then
        assertThat(point.getUserId()).isEqualTo(userId);
        assertThat(point.getAmount()).isZero();
    }

    @Test
    @DisplayName("포인트 적립 시 금액이 증가하고 히스토리가 생성된다")
    void addAmount() {
        // given
        Point point = Point.create(1L);
        PointActionType actionType = PointActionType.SIGN_UP;

        // when
        PointHistory history = point.addAmount(actionType);

        // then
        assertThat(point.getAmount()).isEqualTo(100);
        assertThat(history.getUserId()).isEqualTo(1L);
        assertThat(history.getPointChange()).isEqualTo(100);
        assertThat(history.getReason()).isEqualTo("SIGN_UP");
    }

    @Test
    @DisplayName("포인트 차감 시 금액이 감소하고 히스토리가 생성된다")
    void deductAmount() {
        // given
        Point point = Point.create(1L);
        point.addAmount(PointActionType.SIGN_UP); // 100 포인트 적립

        // when
        PointHistory history = point.deductAmount(PointActionType.BOARD_DELETE);

        // then
        assertThat(point.getAmount()).isEqualTo(90);
        assertThat(history.getPointChange()).isEqualTo(-10);
        assertThat(history.getReason()).isEqualTo("BOARD_DELETE");
    }

    @Test
    @DisplayName("포인트 차감 시 잔액이 부족하면 예외가 발생한다")
    void deductAmount_insufficientPoint() {

        // given
        Point point = Point.create(1L);
        point.addAmount(PointActionType.COMMENT_CREATE); // 5 포인트만 적립
        PointActionType actionType = PointActionType.BOARD_DELETE; // 10 포인트 차감 시도

        // when & then
        assertThatThrownBy(() -> point.deductAmount(actionType))
                .isInstanceOf(PointException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.INSUFFICIENT_POINT);
    }
}