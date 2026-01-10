package woobl0g.pointservice.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.pointservice.point.domain.FailureStatus;
import woobl0g.pointservice.point.domain.PointFailureHistory;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PointFailureHistoryResponseDto {

    private Long id;
    private Long userId;
    private String actionType;
    private FailureStatus status;
    private LocalDateTime failedAt;
    private LocalDateTime resolvedAt;

    public static PointFailureHistoryResponseDto from(PointFailureHistory pointFailureHistory) {
        return new PointFailureHistoryResponseDto(
                pointFailureHistory.getId(),
                pointFailureHistory.getUserId(),
                pointFailureHistory.getActionType(),
                pointFailureHistory.getStatus(),
                pointFailureHistory.getFailedAt(),
                pointFailureHistory.getResolvedAt()
        );
    }
}
