package woobl0g.boardservice.comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private String content;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;  //null 이면 댓글, 아니면 대댓글

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    private Comment(String content, Board board, User user, Comment parent) {
        this.content = content;
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = null;
        this.board = board;
        this.user = user;
        this.parent = parent;
    }

    public static Comment create(String content, Board board, User user, Comment parent) {
        return new Comment(content, board, user, parent);
    }
}
