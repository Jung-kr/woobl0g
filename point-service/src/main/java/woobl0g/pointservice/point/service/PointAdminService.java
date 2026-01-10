package woobl0g.pointservice.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.pointservice.global.exception.PointException;
import woobl0g.pointservice.global.response.ResponseCode;
import woobl0g.pointservice.point.domain.FailureStatus;
import woobl0g.pointservice.point.domain.PointActionType;
import woobl0g.pointservice.point.domain.PointFailureHistory;
import woobl0g.pointservice.point.dto.AddPointRequestDto;
import woobl0g.pointservice.point.dto.PointFailureHistoryResponseDto;
import woobl0g.pointservice.point.repository.PointFailureHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointAdminService {

    private final PointService pointService;
    private final PointFailureHistoryRepository pointFailureHistoryRepository;

    @Transactional(readOnly = true)
    public List<PointFailureHistoryResponseDto> getFailureHistories(Pageable pageable) {
        return pointFailureHistoryRepository.findAllByStatus(FailureStatus.PENDING, pageable)
                .stream()
                .map(PointFailureHistoryResponseDto::from)
                .toList();
    }

    @Transactional
    public void retryFailedPoint(Long failureId) {
        PointFailureHistory pointFailureHistory = pointFailureHistoryRepository.findById(failureId)
                .orElseThrow(() -> new PointException(ResponseCode.ADMIN_POINT_FAILURE_NOT_FOUND));

        if (pointFailureHistory.getStatus() != FailureStatus.PENDING) {
            throw new PointException(ResponseCode.ADMIN_POINT_FAILURE_ALREADY_PROCESSED);
        }

        AddPointRequestDto dto = AddPointRequestDto.of(
                pointFailureHistory.getUserId(),
                PointActionType.valueOf(pointFailureHistory.getActionType())
        );
        pointService.addPoints(dto);
        pointFailureHistory.markAsResolved();
    }

    @Transactional
    public void ignoreFailedPoint(Long failureId) {
        PointFailureHistory pointFailureHistory = pointFailureHistoryRepository.findById(failureId)
                .orElseThrow(() -> new PointException(ResponseCode.ADMIN_POINT_FAILURE_NOT_FOUND));

        pointFailureHistory.markAsIgnored();
    }
}
