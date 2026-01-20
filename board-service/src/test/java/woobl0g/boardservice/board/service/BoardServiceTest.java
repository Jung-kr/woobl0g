package woobl0g.boardservice.board.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import woobl0g.boardservice.board.client.UserClient;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.domain.User;
import woobl0g.boardservice.board.dto.*;
import woobl0g.boardservice.board.repository.BoardRepository;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.response.ResponseCode;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("BoardService 테스트")
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    @DisplayName("게시글 생성 시 정상적으로 생성되고 이벤트가 발행된다")
    void create() {
        // given
        Long userId = 1L;
        CreateBoardRequestDto dto = new CreateBoardRequestDto("제목", "내용");
        Board board = Board.create(dto.getTitle(), dto.getContent(), userId);

        when(boardRepository.save(any(Board.class))).thenReturn(board);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // when
        boardService.create(dto, userId);

        // then
        verify(boardRepository, times(1)).save(any(Board.class));
        verify(kafkaTemplate, times(1)).send(eq("board.created"), anyString());
    }

    @Test
    @DisplayName("게시글 조회 시 정상적으로 조회된다")
    void getBoard() {
        // given
        Long boardId = 1L;
        Board board = Board.create("제목", "내용", 1L);
        UserResponseDto userDto = new UserResponseDto(1L, "test@example.com", "테스터");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(userClient.fetchUser(1L)).thenReturn(Optional.of(userDto));

        // when
        BoardResponseDto result = boardService.getBoard(boardId);

        // then
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getContent()).isEqualTo("내용");
        verify(boardRepository, times(1)).findById(boardId);
        verify(userClient, times(1)).fetchUser(1L);
    }

    @Test
    @DisplayName("게시글 조회 시 게시글이 없으면 예외가 발생한다")
    void getBoard_notFound() {
        // given
        Long boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.getBoard(boardId))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.BOARD_NOT_FOUND);

        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("전체 게시글 조회 시 정상적으로 조회된다")
    void getBoards() {
        // given
        Board board1 = Board.create("제목1", "내용1", 1L);
        Board board2 = Board.create("제목2", "내용2", 2L);
        List<Board> boards = List.of(board1, board2);

        UserResponseDto user1 = new UserResponseDto(1L, "test1@example.com", "테스터1");
        UserResponseDto user2 = new UserResponseDto(2L, "test2@example.com", "테스터2");

        when(boardRepository.findAll()).thenReturn(boards);
        when(userClient.fetchUsers(anyList())).thenReturn(List.of(user1, user2));

        // when
        List<BoardResponseDto> result = boardService.getBoards();

        // then
        assertThat(result).hasSize(2);
        verify(boardRepository, times(1)).findAll();
        verify(userClient, times(1)).fetchUsers(anyList());
    }

    @Test
    @DisplayName("게시글 조회(동기화된 사용자) 시 정상적으로 조회된다")
    void getBoard2() {
        // given
        Long boardId = 1L;
        Board board = mock(Board.class);
        User user = User.create(1L, "테스터", "test@example.com");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(board.getUser()).thenReturn(user);
        when(board.getTitle()).thenReturn("제목");
        when(board.getContent()).thenReturn("내용");

        // when
        BoardResponseDto result = boardService.getBoard2(boardId);

        // then
        assertThat(result.getTitle()).isEqualTo("제목");
        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("게시글 검색 시 정상적으로 조회된다")
    void getBoards2() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Board board = mock(Board.class);
        User user = User.create(1L, "테스터", "test@example.com");
        Page<Board> boardPage = new PageImpl<>(List.of(board), pageable, 1);

        when(boardRepository.findAll(pageable)).thenReturn(boardPage);
        when(board.getUser()).thenReturn(user);
        when(board.getTitle()).thenReturn("제목");
        when(board.getContent()).thenReturn("내용");

        // when
        PageResponse<BoardResponseDto> result = boardService.getBoards2(null, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(boardRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("게시글 삭제 시 정상적으로 삭제된다")
    void delete() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        Board board = mock(Board.class);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        doNothing().when(board).validateOwnership(userId);
        doNothing().when(board).validateModifiable();
        doNothing().when(boardRepository).delete(board);

        // when
        boardService.delete(boardId, userId);

        // then
        verify(boardRepository, times(1)).findById(boardId);
        verify(board, times(1)).validateOwnership(userId);
        verify(board, times(1)).validateModifiable();
        verify(boardRepository, times(1)).delete(board);
    }

    @Test
    @DisplayName("게시글 삭제 시 게시글이 없으면 예외가 발생한다")
    void delete_notFound() {
        // given
        Long boardId = 1L;
        Long userId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.delete(boardId, userId))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.BOARD_NOT_FOUND);

        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("게시글 삭제 시 작성자가 아니면 예외가 발생한다")
    void delete_forbidden() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        Long differentUserId = 2L;
        Board board = Board.create("제목", "내용", userId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // when & then
        assertThatThrownBy(() -> boardService.delete(boardId, differentUserId))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.BOARD_MODIFY_FORBIDDEN);

        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("게시글 수정 시 정상적으로 수정된다")
    void update() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        Board board = mock(Board.class);
        UpdateBoardRequestDto dto = new UpdateBoardRequestDto("수정 제목", "수정 내용");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        doNothing().when(board).validateOwnership(userId);
        doNothing().when(board).validateModifiable();
        doNothing().when(board).update(dto.getTitle(), dto.getContent());

        // when
        boardService.update(boardId, dto, userId);

        // then
        verify(boardRepository, times(1)).findById(boardId);
        verify(board, times(1)).validateOwnership(userId);
        verify(board, times(1)).validateModifiable();
        verify(board, times(1)).update(dto.getTitle(), dto.getContent());
    }

    @Test
    @DisplayName("게시글 수정 시 게시글이 없으면 예외가 발생한다")
    void update_notFound() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        UpdateBoardRequestDto dto = new UpdateBoardRequestDto("제목", "내용");

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.update(boardId, dto, userId))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.BOARD_NOT_FOUND);

        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("게시글 수정 시 작성자가 아니면 예외가 발생한다")
    void update_forbidden() {
        // given
        Long boardId = 1L;
        Long userId = 1L;
        Long differentUserId = 2L;
        Board board = Board.create("제목", "내용", userId);
        UpdateBoardRequestDto dto = new UpdateBoardRequestDto("수정 제목", "수정 내용");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // when & then
        assertThatThrownBy(() -> boardService.update(boardId, dto, differentUserId))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.BOARD_MODIFY_FORBIDDEN);

        verify(boardRepository, times(1)).findById(boardId);
    }
}
