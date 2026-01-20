package woobl0g.pointservice.point.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import woobl0g.pointservice.global.exception.PointException;
import woobl0g.pointservice.global.response.ResponseCode;
import woobl0g.pointservice.point.domain.FailureStatus;
import woobl0g.pointservice.point.domain.PointActionType;
import woobl0g.pointservice.point.domain.PointFailureHistory;
import woobl0g.pointservice.point.dto.AddPointRequestDto;
import woobl0g.pointservice.point.dto.PageResponse;
import woobl0g.pointservice.point.dto.PointFailureHistoryResponseDto;
import woobl0g.pointservice.point.repository.PointFailureHistoryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PointAdminService 테스트")
@ExtendWith(MockitoExtension.class)
class PointAdminServiceTest {

    @InjectMocks
    private PointAdminService pointAdminService;

    @Mock
    private PointService pointService;

    @Mock
    private PointFailureHistoryRepository pointFailureHistoryRepository;

    @Test
    @DisplayName("실패 이력 조회 시 PENDING 상태만 조회된다")
    void getFailureHistories() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        PointFailureHistory history = PointFailureHistory.create(1L, "SIGN_UP");
        Page<PointFailureHistory> historyPage = new PageImpl<>(List.of(history), pageable, 1);

        when(pointFailureHistoryRepository.findAllByStatus(FailureStatus.PENDING, pageable)).thenReturn(historyPage);

        // when
        PageResponse<PointFailureHistoryResponseDto> result = pointAdminService.getFailureHistories(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(pointFailureHistoryRepository, times(1)).findAllByStatus(FailureStatus.PENDING, pageable);
    }

    @Test
    @DisplayName("실패 포인트 재처리 시 정상적으로 처리된다")
    void retryFailedPoint() {
        // given
        Long failureId = 1L;
        Long userId = 1L;
        PointFailureHistory history = PointFailureHistory.create(userId, "SIGN_UP");

        when(pointFailureHistoryRepository.findById(failureId)).thenReturn(Optional.of(history));
        doNothing().when(pointService).addPoints(any(AddPointRequestDto.class));

        // when
        pointAdminService.retryFailedPoint(failureId);

        // then
        assertThat(history.getStatus()).isEqualTo(FailureStatus.RESOLVED);
        assertThat(history.getResolvedAt()).isNotNull();
        verify(pointFailureHistoryRepository, times(1)).findById(failureId);
        verify(pointService, times(1)).addPoints(any(AddPointRequestDto.class));
    }

    @Test
    @DisplayName("실패 포인트 재처리 시 이력이 없으면 예외가 발생한다")
    void retryFailedPoint_notFound() {
        // given
        Long failureId = 1L;

        when(pointFailureHistoryRepository.findById(failureId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointAdminService.retryFailedPoint(failureId))
                .isInstanceOf(PointException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.ADMIN_POINT_FAILURE_NOT_FOUND);

        verify(pointFailureHistoryRepository, times(1)).findById(failureId);
    }

    @Test
    @DisplayName("실패 포인트 재처리 시 이미 처리된 이력이면 예외가 발생한다")
    void retryFailedPoint_alreadyProcessed() {
        // given
        Long failureId = 1L;
        PointFailureHistory history = PointFailureHistory.create(1L, "SIGN_UP");
        history.markAsResolved(); // 이미 처리됨

        when(pointFailureHistoryRepository.findById(failureId)).thenReturn(Optional.of(history));

        // when & then
        assertThatThrownBy(() -> pointAdminService.retryFailedPoint(failureId))
                .isInstanceOf(PointException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.ADMIN_POINT_FAILURE_ALREADY_PROCESSED);

        verify(pointFailureHistoryRepository, times(1)).findById(failureId);
    }

    @Test
    @DisplayName("실패 포인트 무시 처리 시 정상적으로 처리된다")
    void ignoreFailedPoint() {
        // given
        Long failureId = 1L;
        PointFailureHistory history = PointFailureHistory.create(1L, "SIGN_UP");

        when(pointFailureHistoryRepository.findById(failureId)).thenReturn(Optional.of(history));

        // when
        pointAdminService.ignoreFailedPoint(failureId);

        // then
        assertThat(history.getStatus()).isEqualTo(FailureStatus.IGNORED);
        verify(pointFailureHistoryRepository, times(1)).findById(failureId);
    }

    @Test
    @DisplayName("실패 포인트 무시 처리 시 이력이 없으면 예외가 발생한다")
    void ignoreFailedPoint_notFound() {
        // given
        Long failureId = 1L;

        when(pointFailureHistoryRepository.findById(failureId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointAdminService.ignoreFailedPoint(failureId))
                .isInstanceOf(PointException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.ADMIN_POINT_FAILURE_NOT_FOUND);

        verify(pointFailureHistoryRepository, times(1)).findById(failureId);
    }
}
