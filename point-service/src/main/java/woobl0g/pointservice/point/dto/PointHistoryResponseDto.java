package woobl0g.pointservice.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.pointservice.point.domain.PointHistory;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PointHistoryResponseDto {

    private Long id;
    private Long userId;
    private int pointChange;
    private String reason;
    private LocalDateTime createdAt;

    public static PointHistoryResponseDto from(PointHistory history) {
        return new PointHistoryResponseDto(
                history.getId(),
                history.getUserId(),
                history.getPointChange(),
                history.getReason(),
                history.getCreatedAt()
        );
    }
}
