package woobl0g.boardservice.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.boardservice.board.client.UserClient;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.dto.BoardResponseDto;
import woobl0g.boardservice.board.dto.CreateBoardRequestDto;
import woobl0g.boardservice.board.dto.UserInfoDto;
import woobl0g.boardservice.board.dto.UserResponseDto;
import woobl0g.boardservice.board.repository.BoardRepository;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.response.ResponseCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        UserInfoDto userInfoDto = userClient.fetchUser(board.getUserId())
                .map(dto -> new UserInfoDto(dto.getEmail(), dto.getName()))
                .orElse(null);

        return new BoardResponseDto(board.getTitle(), board.getContent(), userInfoDto);
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> getBoards() {
        List<Board> boards = boardRepository.findAll();

        List<Long> userIds = boards.stream()
                .map(Board::getUserId)
                .distinct()
                .toList();

        List<UserResponseDto> userResponseDtos = userClient.fetchUsers(userIds);

        Map<Long, UserInfoDto> userInfoMap = new HashMap<>();
        for (UserResponseDto userResponseDto : userResponseDtos) {
            Long userId = userResponseDto.getUserId();
            String email = userResponseDto.getEmail();
            String name = userResponseDto.getName();
            userInfoMap.put(userId, new UserInfoDto(email, name));
        }

        return boards.stream()
                .map(board -> new BoardResponseDto(
                        board.getTitle(),
                        board.getContent(),
                        userInfoMap.get(board.getUserId())
                ))
                .toList();
    }
}
