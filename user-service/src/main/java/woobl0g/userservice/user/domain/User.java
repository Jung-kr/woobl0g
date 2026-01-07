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

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;

    private User(String email, String name, String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public static User create(String email, String name, String password) {
        return new User(email, name, password, Role.USER);
    }
}
