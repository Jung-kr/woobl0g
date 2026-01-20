package woobl0g.pointservice.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import woobl0g.pointservice.global.exception.PointException;
import woobl0g.pointservice.global.response.ResponseCode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.Assertions.*;

@DisplayName("PointFailureHistory 도메인 테스트")
class PointFailureHistoryTest {

    @Test
    @DisplayName("실패 이력 생성 시 상태는 PENDING이다")
    void create() {
        // given
        Long userId = 1L;
        String actionType = "SIGN_UP";

        // when
        PointFailureHistory history = PointFailureHistory.create(userId, actionType);

        // then
        assertThat(history.getUserId()).isEqualTo(userId);
        assertThat(history.getActionType()).isEqualTo(actionType);
        assertThat(history.getStatus()).isEqualTo(FailureStatus.PENDING);
        assertThat(history.getFailedAt()).isNotNull();
        assertThat(history.getResolvedAt()).isNull();
    }

    @Test
    @DisplayName("실패 이력을 해결 처리하면 상태가 RESOLVED로 변경된다")
    void markAsResolved() {
        // given
        PointFailureHistory history = PointFailureHistory.create(1L, "SIGN_UP");

        // when
        history.markAsResolved();

        // then
        assertThat(history.getStatus()).isEqualTo(FailureStatus.RESOLVED);
        assertThat(history.getResolvedAt()).isNotNull();
    }

    @Test
    @DisplayName("실패 이력을 무시 처리하면 상태가 IGNORED로 변경된다")
    void markAsIgnored() {
        // given
        PointFailureHistory history = PointFailureHistory.create(1L, "SIGN_UP");

        // when
        history.markAsIgnored();

        // then
        assertThat(history.getStatus()).isEqualTo(FailureStatus.IGNORED);
    }

    @Test
    @DisplayName("PENDING 상태일 때는 재처리 검증이 통과한다")
    void validateRetryable_pending() {
        // given
        PointFailureHistory history = PointFailureHistory.create(1L, "SIGN_UP");

        // when & then
        assertThatCode(() -> history.validateRetryable())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("RESOLVED 상태일 때는 재처리 검증 시 예외가 발생한다")
    void validateRetryable_resolved() {
        // given
        PointFailureHistory history = PointFailureHistory.create(1L, "SIGN_UP");
        history.markAsResolved();

        // when & then
        assertThatThrownBy(() -> history.validateRetryable())
                .isInstanceOf(PointException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.ADMIN_POINT_FAILURE_ALREADY_PROCESSED);
    }

    @Test
    @DisplayName("IGNORED 상태일 때는 재처리 검증 시 예외가 발생한다")
    void validateRetryable_ignored() {
        // given
        PointFailureHistory history = PointFailureHistory.create(1L, "SIGN_UP");
        history.markAsIgnored();

        // when & then
        assertThatThrownBy(() -> history.validateRetryable())
                .isInstanceOf(PointException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.ADMIN_POINT_FAILURE_ALREADY_PROCESSED);
    }
}