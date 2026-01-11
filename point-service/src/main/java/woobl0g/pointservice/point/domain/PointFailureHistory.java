package woobl0g.pointservice.point.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woobl0g.pointservice.global.exception.PointException;
import woobl0g.pointservice.global.response.ResponseCode;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_failure_history")
public class PointFailureHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String actionType;

    @Enumerated(EnumType.STRING)
    private FailureStatus status;

    private LocalDateTime failedAt;
    private LocalDateTime resolvedAt;

    private PointFailureHistory(Long userId, String actionType) {
        this.userId = userId;
        this.actionType = actionType;
        this.status = FailureStatus.PENDING;
        this.failedAt = LocalDateTime.now();
    }

    public static PointFailureHistory create(Long userId, String actionType) {
        return new PointFailureHistory(userId, actionType);
    }

    public void markAsResolved() {
        this.status = FailureStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
    }

    public void markAsIgnored() {
        this.status = FailureStatus.IGNORED;
    }

    public void validateRetryable() {
        if (this.status != FailureStatus.PENDING) {
            throw new PointException(ResponseCode.ADMIN_POINT_FAILURE_ALREADY_PROCESSED);
        }
    }
}
