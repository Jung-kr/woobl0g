package woobl0g.boardservice.board.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woobl0g.boardservice.comment.domain.Comment;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.response.ResponseCode;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;
    private String title;
    private String content;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id")
    private Long userId;

    private Board(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Board create(String title, String content, Long userId) {
        return new Board(title, content, userId);
    }

    public void update(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canModify() {
        return ChronoUnit.DAYS.between(createdAt, LocalDateTime.now()) >= 1;
    }

    public void validateOwnership(Long requestUserId) {
        if (!userId.equals(requestUserId)) {
            throw new BoardException(ResponseCode.BOARD_MODIFY_FORBIDDEN);
        }
    }

    public void validateModifiable() {
        if (!canModify()) {
            throw new BoardException(ResponseCode.BOARD_MODIFY_TOO_EARLY);
        }
    }
}
