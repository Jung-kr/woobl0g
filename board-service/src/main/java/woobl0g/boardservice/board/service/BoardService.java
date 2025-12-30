package woobl0g.boardservice.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.boardservice.board.client.UserClient;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.dto.BoardResponseDto;
import woobl0g.boardservice.board.dto.CreateBoardRequestDto;
import woobl0g.boardservice.board.dto.UserDto;
import woobl0g.boardservice.board.dto.UserResponseDto;
import woobl0g.boardservice.board.repository.BoardRepository;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.response.ResponseCode;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserClient userClient;

    @Transactional
    public void create(CreateBoardRequestDto dto) {
        Board board = Board.create(dto.getTitle(), dto.getContent(), dto.getUserId());
        boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(ResponseCode.BOARD_NOT_FOUND));

        // user-service 로부터 사용자 정보 불러오기 -> user-service가 null 반환하거나 서버 에러 시 Optional.empty() 반환 -> Optional.empty() 반환하면 userDto에 null
        UserDto userDto = userClient.fetchUser(board.getUserId())
                .map(dto -> new UserDto(dto.getEmail(), dto.getName()))
                .orElse(null);

        return new BoardResponseDto(board.getTitle(), board.getContent(), userDto);
    }
}
