package woobl0g.boardservice.board.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.response.ResponseCode;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Board 도메인 테스트")
class BoardTest {

    @Test
    @DisplayName("게시글 생성 시 정상적으로 생성된다")
    void create() {
        // given
        String title = "테스트 제목";
        String content = "테스트 내용";
        Long userId = 1L;

        // when
        Board board = Board.create(title, content, userId);

        // then
        assertThat(board.getTitle()).isEqualTo(title);
        assertThat(board.getContent()).isEqualTo(content);
        assertThat(board.getUserId()).isEqualTo(userId);
        assertThat(board.getCreatedAt()).isNotNull();
        assertThat(board.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("게시글 수정 시 제목과 내용이 변경된다")
    void update() {
        // given
        Board board = Board.create("원본 제목", "원본 내용", 1L);
        String newTitle = "수정된 제목";
        String newContent = "수정된 내용";

        // when
        board.update(newTitle, newContent);

        // then
        assertThat(board.getTitle()).isEqualTo(newTitle);
        assertThat(board.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("게시글 수정 시 null 값은 변경되지 않는다")
    void update_withNull() {
        // given
        Board board = Board.create("원본 제목", "원본 내용", 1L);

        // when
        board.update(null, null);

        // then
        assertThat(board.getTitle()).isEqualTo("원본 제목");
        assertThat(board.getContent()).isEqualTo("원본 내용");
    }

    @Test
    @DisplayName("작성자 검증 시 작성자가 일치하면 예외가 발생하지 않는다")
    void validateOwnership_success() {
        // given
        Long userId = 1L;
        Board board = Board.create("제목", "내용", userId);

        // when & then
        assertThatCode(() -> board.validateOwnership(userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("작성자 검증 시 작성자가 다르면 예외가 발생한다")
    void validateOwnership_fail() {
        // given
        Board board = Board.create("제목", "내용", 1L);
        Long differentUserId = 2L;

        // when & then
        assertThatThrownBy(() -> board.validateOwnership(differentUserId))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.BOARD_MODIFY_FORBIDDEN);
    }

    @Test
    @DisplayName("수정 가능 여부 검증 시 1일 미만이면 예외가 발생한다")
    void validateModifiable_fail() {
        // given
        Board board = Board.create("제목", "내용", 1L);

        // when & then
        assertThatThrownBy(() -> board.validateModifiable())
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.BOARD_MODIFY_TOO_EARLY);
    }
}
