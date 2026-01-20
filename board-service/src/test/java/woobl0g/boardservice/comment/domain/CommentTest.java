package woobl0g.boardservice.comment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.domain.User;
import woobl0g.boardservice.global.exception.CommentException;
import woobl0g.boardservice.global.response.ResponseCode;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Comment 도메인 테스트")
class CommentTest {

    @Test
    @DisplayName("댓글 생성 시 정상적으로 생성된다")
    void create_comment() {
        // given
        String content = "테스트 댓글";
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(1L, "테스터", "test@example.com");

        // when
        Comment comment = Comment.create(content, board, user, null);

        // then
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getBoard()).isEqualTo(board);
        assertThat(comment.getUser()).isEqualTo(user);
        assertThat(comment.getDepth()).isZero();
        assertThat(comment.isDeleted()).isFalse();
        assertThat(comment.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("대댓글 생성 시 depth가 1이 된다")
    void create_reply() {
        // given
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(1L, "테스터", "test@example.com");
        Comment parent = Comment.create("부모 댓글", board, user, null);

        // when
        Comment reply = Comment.create("대댓글", board, user, parent);

        // then
        assertThat(reply.getDepth()).isEqualTo(1);
        assertThat(reply.getParent()).isEqualTo(parent);
    }

    @Test
    @DisplayName("대댓글의 대댓글 생성 시 예외가 발생한다")
    void create_reply_depthExceeded() {
        // given
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(1L, "테스터", "test@example.com");
        Comment parent = Comment.create("부모 댓글", board, user, null);
        Comment reply = Comment.create("대댓글", board, user, parent);

        // when & then
        assertThatThrownBy(() -> Comment.create("대댓글의 대댓글", board, user, reply))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.REPLY_DEPTH_EXCEEDED);
    }

    @Test
    @DisplayName("댓글 수정 시 내용이 변경된다")
    void update() {
        // given
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(1L, "테스터", "test@example.com");
        Comment comment = Comment.create("원본 내용", board, user, null);
        String newContent = "수정된 내용";

        // when
        comment.update(newContent);

        // then
        assertThat(comment.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("댓글 소프트 삭제 시 삭제 상태로 변경된다")
    void softDelete() {
        // given
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(1L, "테스터", "test@example.com");
        Comment comment = Comment.create("댓글 내용", board, user, null);

        // when
        comment.softDelete();

        // then
        assertThat(comment.isDeleted()).isTrue();
        assertThat(comment.getContent()).isEqualTo("삭제된 댓글입니다.");
        assertThat(comment.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("작성자 검증 시 작성자가 일치하면 예외가 발생하지 않는다")
    void validateOwnership_success() {
        // given
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(1L, "테스터", "test@example.com");
        Comment comment = Comment.create("댓글", board, user, null);

        // when & then
        assertThatCode(() -> comment.validateOwnership(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("작성자 검증 시 작성자가 다르면 예외가 발생한다")
    void validateOwnership_fail() {
        // given
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(1L, "테스터", "test@example.com");
        Comment comment = Comment.create("댓글", board, user, null);

        // when & then
        assertThatThrownBy(() -> comment.validateOwnership(2L))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.COMMENT_MODIFY_FORBIDDEN);
    }

    @Test
    @DisplayName("수정 가능 여부 검증 시 1일 미만이면 예외가 발생한다")
    void validateModifiable_fail() {
        // given
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(1L, "테스터", "test@example.com");
        Comment comment = Comment.create("댓글", board, user, null);

        // when & then
        assertThatThrownBy(() -> comment.validateModifiable())
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.COMMENT_MODIFY_TOO_EARLY);
    }
}
