package woobl0g.userservice.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "activity_score_history")
public class ActivityScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private int scoreChange;
    private String reason;
    private LocalDateTime createdAt;

    private ActivityScoreHistory(Long userId, int scoreChange, String reason) {
        this.userId = userId;
        this.scoreChange = scoreChange;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    public static ActivityScoreHistory create(Long userId, int scoreChange, String reason) {
        return new ActivityScoreHistory(userId, scoreChange, reason);
    }
}
