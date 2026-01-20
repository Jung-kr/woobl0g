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
import woobl0g.pointservice.point.client.UserClient;
import woobl0g.pointservice.point.domain.Point;
import woobl0g.pointservice.point.domain.PointActionType;
import woobl0g.pointservice.point.domain.PointHistory;
import woobl0g.pointservice.point.dto.*;
import woobl0g.pointservice.point.repository.PointHistoryRepository;
import woobl0g.pointservice.point.repository.PointRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PointService 테스트")
@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private UserClient userClient;

    @Test
    @DisplayName("포인트 적립 시 기존 포인트가 있으면 적립된다")
    void addPoints_existingPoint() {
        // given
        Long userId = 1L;
        Point point = Point.create(userId);
        AddPointRequestDto dto = AddPointRequestDto.of(userId, PointActionType.SIGN_UP);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(null);

        // when
        pointService.addPoints(dto);

        // then
        assertThat(point.getAmount()).isEqualTo(100);
        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("포인트 적립 시 포인트가 없으면 신규 생성 후 적립된다")
    void addPoints_newPoint() {
        // given
        Long userId = 1L;
        Point point = Point.create(userId);
        AddPointRequestDto dto = AddPointRequestDto.of(userId, PointActionType.SIGN_UP);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(pointRepository.save(any(Point.class))).thenReturn(point);
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(null);

        // when
        pointService.addPoints(dto);

        // then
        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, times(1)).save(any(Point.class));
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("포인트 차감 시 정상적으로 차감된다")
    void deductPoints() {
        // given
        Long userId = 1L;
        Point point = Point.create(1L);
        point.addAmount(PointActionType.SIGN_UP);
        DeductPointRequestDto dto = DeductPointRequestDto.of(userId, PointActionType.BOARD_DELETE);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(null);

        // when
        pointService.deductPoints(dto);

        // then
        assertThat(point.getAmount()).isEqualTo(90);
        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("포인트 차감 시 포인트가 없으면 예외가 발생한다")
    void deductPoints_pointNotFound() {
        // given
        Long userId = 1L;
        DeductPointRequestDto dto = DeductPointRequestDto.of(userId, PointActionType.BOARD_DELETE);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.deductPoints(dto))
                .isInstanceOf(PointException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.POINT_NOT_FOUND);

        verify(pointRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("포인트 이력 조회 시 정상적으로 조회된다")
    void getPointHistory() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        PointHistory history = PointHistory.create(userId, 100, "SIGN_UP");
        Page<PointHistory> historyPage = new PageImpl<>(List.of(history), pageable, 1);

        when(pointRepository.existsByUserId(userId)).thenReturn(true);
        when(pointHistoryRepository.findByUserId(userId, pageable)).thenReturn(historyPage);

        // when
        PageResponse<PointHistoryResponseDto> result = pointService.getPointHistory(userId, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(pointRepository, times(1)).existsByUserId(userId);
        verify(pointHistoryRepository, times(1)).findByUserId(userId, pageable);
    }

    @Test
    @DisplayName("포인트 이력 조회 시 포인트가 없으면 예외가 발생한다")
    void getPointHistory_pointNotFound() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(pointRepository.existsByUserId(userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> pointService.getPointHistory(userId, pageable))
                .isInstanceOf(PointException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.POINT_NOT_FOUND);

        verify(pointRepository, times(1)).existsByUserId(userId);
    }

    @Test
    @DisplayName("포인트 랭킹 조회 시 정상적으로 조회된다")
    void getPointRanking() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Point point1 = Point.create(1L);
        Point point2 = Point.create(2L);
        point1.addAmount(PointActionType.SIGN_UP);
        point2.addAmount(PointActionType.BOARD_CREATE);

        Page<Point> pointPage = new PageImpl<>(List.of(point1, point2), pageable, 2);

        UserResponseDto user1 = new UserResponseDto(1L, "user1@test.com", "사용자1");
        UserResponseDto user2 = new UserResponseDto(2L, "user2@test.com", "사용자2");

        when(pointRepository.findAll(pageable)).thenReturn(pointPage);
        when(userClient.fetchUsers(List.of(1L, 2L))).thenReturn(List.of(user1, user2));

        // when
        List<PointRankingResponseDto> result = pointService.getPointRanking(pageable);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRank()).isEqualTo(1);
        assertThat(result.get(0).getAmount()).isEqualTo(100);
        assertThat(result.get(1).getRank()).isEqualTo(2);
        assertThat(result.get(1).getAmount()).isEqualTo(10);
        verify(pointRepository, times(1)).findAll(pageable);
        verify(userClient, times(1)).fetchUsers(List.of(1L, 2L));
    }

}