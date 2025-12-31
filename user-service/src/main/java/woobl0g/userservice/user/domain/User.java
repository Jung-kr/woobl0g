package woobl0g.userservice.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String email;
    private String name;
    private String password;
    private int activityScore;
    private LocalDateTime createdAt;

    private User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.activityScore = 0;
        this.createdAt = LocalDateTime.now();
    }

    public static User create(String email, String name, String password) {
        return new User(email, name, password);
    }

    // 활동 점수 적립
    public void addActivityScore(int activityScore) {
        this.activityScore += activityScore;
    }
}
