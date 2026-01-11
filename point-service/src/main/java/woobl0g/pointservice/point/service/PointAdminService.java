package woobl0g.pointservice.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PointAdminService {

    private final PointService pointService;
    private final PointFailureHistoryRepository pointFailureHistoryRepository;

    @Transactional(readOnly = true)
    public PageResponse<PointFailureHistoryResponseDto> getFailureHistories(Pageable pageable) {
        log.debug("포인트 실패 이력 조회: page={}", pageable.getPageNumber());
        
        Page<PointFailureHistory> failureHistories = pointFailureHistoryRepository.findAllByStatus(FailureStatus.PENDING, pageable);

        return PageResponse.of(failureHistories.map(PointFailureHistoryResponseDto::from));
    }

    @Transactional
    public void retryFailedPoint(Long failureId) {
        log.info("포인트 재처리 시도: failureId={}", failureId);
        
        PointFailureHistory pointFailureHistory = pointFailureHistoryRepository.findById(failureId)
                .orElseThrow(() -> new PointException(ResponseCode.ADMIN_POINT_FAILURE_NOT_FOUND));

        pointFailureHistory.validateRetryable();

        AddPointRequestDto dto = AddPointRequestDto.of(
                pointFailureHistory.getUserId(),
                PointActionType.valueOf(pointFailureHistory.getActionType())
        );
        pointService.addPoints(dto);
        pointFailureHistory.markAsResolved();
        
        log.info("포인트 재처리 완료: failureId={}, userId={}", failureId, pointFailureHistory.getUserId());
    }

    @Transactional
    public void ignoreFailedPoint(Long failureId) {
        log.info("포인트 실패 무시 처리: failureId={}", failureId);
        
        PointFailureHistory pointFailureHistory = pointFailureHistoryRepository.findById(failureId)
                .orElseThrow(() -> new PointException(ResponseCode.ADMIN_POINT_FAILURE_NOT_FOUND));

        pointFailureHistory.markAsIgnored();
        
        log.info("포인트 실패 무시 완료: failureId={}", failureId);
    }
}
