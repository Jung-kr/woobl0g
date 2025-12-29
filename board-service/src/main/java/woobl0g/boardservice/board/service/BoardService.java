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

        UserResponseDto dto = userClient.fetchUser(board.getUserId());

        UserDto userDto = new UserDto(dto.getEmail(), dto.getName());

        return new BoardResponseDto(board.getTitle(), board.getContent(), userDto);
    }
}
