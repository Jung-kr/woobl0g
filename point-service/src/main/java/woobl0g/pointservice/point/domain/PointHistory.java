package woobl0g.pointservice.point.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_history")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private int pointChange;
    private String reason;
    private LocalDateTime createdAt;

    private PointHistory(Long userId, int pointChange, String reason) {
        this.userId = userId;
        this.pointChange = pointChange;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    public static PointHistory create(Long userId, int pointChange, String reason) {
        return new PointHistory(userId, pointChange, reason);
    }
}
