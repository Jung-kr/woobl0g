package woobl0g.userservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.userservice.user.domain.ActivityScoreHistory;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ActivityScoreHistoryResponseDto {

    private Long id;
    private Long userId;
    private int scoreChange;
    private String reason;
    private LocalDateTime createdAt;

    public static ActivityScoreHistoryResponseDto from(ActivityScoreHistory history) {
        return new ActivityScoreHistoryResponseDto(
                history.getId(),
                history.getUserId(),
                history.getScoreChange(),
                history.getReason(),
                history.getCreatedAt()
        );
    }
}
