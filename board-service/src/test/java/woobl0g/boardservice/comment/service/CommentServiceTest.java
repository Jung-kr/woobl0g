package woobl0g.boardservice.comment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.domain.User;
import woobl0g.boardservice.board.dto.UpdateCommentRequestDto;
import woobl0g.boardservice.board.repository.BoardRepository;
import woobl0g.boardservice.board.repository.UserRepository;
import woobl0g.boardservice.comment.domain.Comment;
import woobl0g.boardservice.comment.dto.CommentResponseDto;
import woobl0g.boardservice.comment.dto.CreateCommentRequestDto;
import woobl0g.boardservice.comment.repository.CommentRepository;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.exception.CommentException;
import woobl0g.boardservice.global.exception.UserException;
import woobl0g.boardservice.global.response.ResponseCode;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("CommentService 테스트")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    @DisplayName("댓글 생성 시 정상적으로 생성되고 이벤트가 발행된다")
    void create() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        CreateCommentRequestDto dto = new CreateCommentRequestDto("댓글 내용", null);
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(userId, "테스터", "test@example.com");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(null);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // when
        commentService.create(boardId, dto, userId);

        // then
        verify(boardRepository, times(1)).findById(boardId);
        verify(userRepository, times(1)).findById(userId);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(kafkaTemplate, times(1)).send(eq("comment.created"), anyString());
    }

    @Test
    @DisplayName("댓글 생성 시 게시글이 없으면 예외가 발생한다")
    void create_boardNotFound() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        CreateCommentRequestDto dto = new CreateCommentRequestDto("댓글 내용", null);

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.create(boardId, dto, userId))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.BOARD_NOT_FOUND);

        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("댓글 생성 시 사용자가 없으면 예외가 발생한다")
    void create_userNotFound() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        CreateCommentRequestDto dto = new CreateCommentRequestDto("댓글 내용", null);
        Board board = Board.create("제목", "내용", 1L);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.create(boardId, dto, userId))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.USER_NOT_FOUND);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("대댓글 생성 시 부모 댓글을 찾아 생성된다")
    void create_reply() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        Long parentId = 1L;
        CreateCommentRequestDto dto = new CreateCommentRequestDto("대댓글 내용", parentId);
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(userId, "테스터", "test@example.com");
        Comment parent = Comment.create("부모 댓글", board, user, null);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(commentRepository.save(any(Comment.class))).thenReturn(null);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // when
        commentService.create(boardId, dto, userId);

        // then
        verify(commentRepository, times(1)).findById(parentId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("대댓글 생성 시 부모 댓글이 없으면 예외가 발생한다")
    void create_parentNotFound() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        Long parentId = 999L;
        CreateCommentRequestDto dto = new CreateCommentRequestDto("대댓글 내용", parentId);
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(userId, "테스터", "test@example.com");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findById(parentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.create(boardId, dto, userId))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.COMMENT_NOT_FOUND);

        verify(commentRepository, times(1)).findById(parentId);
    }

    @Test
    @DisplayName("댓글 목록 조회 시 정상적으로 조회된다")
    void getComments() {
        // given
        Long boardId = 1L;
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(1L, "테스터", "test@example.com");
        Comment comment = Comment.create("댓글", board, user, null);

        when(commentRepository.findByBoardIdWithReplies(boardId)).thenReturn(List.of(comment));

        // when
        List<CommentResponseDto> result = commentService.getComments(boardId);

        // then
        assertThat(result).hasSize(1);
        verify(commentRepository, times(1)).findByBoardIdWithReplies(boardId);
    }

    @Test
    @DisplayName("댓글 삭제 시 자식이 없으면 완전 삭제된다")
    void delete_hardDelete() {
        // given
        Long commentId = 1L;
        Long userId = 1L;
        Comment comment = mock(Comment.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(comment).validateOwnership(userId);
        doNothing().when(comment).validateModifiable();
        when(comment.shouldSoftDelete()).thenReturn(false);
        doNothing().when(commentRepository).delete(comment);

        // when
        commentService.delete(commentId, userId);

        // then
        verify(commentRepository, times(1)).findById(commentId);
        verify(comment, times(1)).validateOwnership(userId);
        verify(comment, times(1)).validateModifiable();
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    @DisplayName("댓글 삭제 시 댓글이 없으면 예외가 발생한다")
    void delete_notFound() {
        // given
        Long commentId = 1L;
        Long userId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.delete(commentId, userId))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.COMMENT_NOT_FOUND);

        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    @DisplayName("댓글 삭제 시 작성자가 아니면 예외가 발생한다")
    void delete_forbidden() {
        // given
        Long commentId = 1L;
        Long userId = 1L;
        Long differentUserId = 2L;
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(userId, "테스터", "test@example.com");
        Comment comment = Comment.create("댓글", board, user, null);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.delete(commentId, differentUserId))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.COMMENT_MODIFY_FORBIDDEN);

        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    @DisplayName("댓글 수정 시 정상적으로 수정된다")
    void update() {
        // given
        Long commentId = 1L;
        Long userId = 1L;
        Comment comment = mock(Comment.class);
        UpdateCommentRequestDto dto = new UpdateCommentRequestDto("수정된 댓글");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(comment).validateOwnership(userId);
        doNothing().when(comment).validateModifiable();
        doNothing().when(comment).update(dto.getContent());

        // when
        commentService.update(commentId, dto, userId);

        // then
        verify(commentRepository, times(1)).findById(commentId);
        verify(comment, times(1)).validateOwnership(userId);
        verify(comment, times(1)).validateModifiable();
        verify(comment, times(1)).update(dto.getContent());
    }

    @Test
    @DisplayName("댓글 수정 시 댓글이 없으면 예외가 발생한다")
    void update_notFound() {
        // given
        Long commentId = 1L;
        Long userId = 1L;
        UpdateCommentRequestDto dto = new UpdateCommentRequestDto("수정된 댓글");

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.update(commentId, dto, userId))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.COMMENT_NOT_FOUND);

        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    @DisplayName("댓글 수정 시 작성자가 아니면 예외가 발생한다")
    void update_forbidden() {
        // given
        Long commentId = 1L;
        Long userId = 1L;
        Long differentUserId = 2L;
        Board board = Board.create("제목", "내용", 1L);
        User user = User.create(userId, "테스터", "test@example.com");
        Comment comment = Comment.create("댓글", board, user, null);
        UpdateCommentRequestDto dto = new UpdateCommentRequestDto("수정된 댓글");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.update(commentId, dto, differentUserId))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.COMMENT_MODIFY_FORBIDDEN);

        verify(commentRepository, times(1)).findById(commentId);
    }
}
