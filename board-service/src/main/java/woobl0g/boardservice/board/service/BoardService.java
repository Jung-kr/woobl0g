package woobl0g.boardservice.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.boardservice.board.client.UserClient;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.dto.*;
import woobl0g.boardservice.board.event.BoardCreatedEvent;
import woobl0g.boardservice.board.repository.BoardRepository;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.response.ResponseCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final UserClient userClient;
    private final BoardRepository boardRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void create(CreateBoardRequestDto dto, Long userId) {
        log.info("게시글 생성 시작: userId={}, title={}", userId, dto.getTitle());
        
        Board board = Board.create(dto.getTitle(), dto.getContent(), userId);
        boardRepository.save(board);

        BoardCreatedEvent boardCreatedEvent = BoardCreatedEvent.of(userId, "BOARD_CREATE", 10);
        kafkaTemplate.send("board.created", boardCreatedEvent.toJson());
        
        log.info("게시글 생성 완료 및 이벤트 발행: boardId={}, userId={}", board.getBoardId(), userId);
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(Long boardId) {
        log.debug("게시글 조회: boardId={}", boardId);
        
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(ResponseCode.BOARD_NOT_FOUND));

        // user-service 로부터 사용자 정보 불러오기 -> user-service가 null 반환하거나 서버 에러 시 Optional.empty() 반환 -> Optional.empty() 반환하면 userDto에 null
        UserInfoDto userInfoDto = userClient.fetchUser(board.getUserId())
                .map(dto -> UserInfoDto.of(dto.getEmail(), dto.getName()))
                .orElse(null);

        return BoardResponseDto.from(board, userInfoDto);
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> getBoards() {
        log.debug("전체 게시글 조회 시작");
        
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
            userInfoMap.put(userId, UserInfoDto.of(email, name));
        }

        return boards.stream()
                .map(board -> BoardResponseDto.from(board, userInfoMap.get(board.getUserId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoard2(Long boardId) {
        log.debug("게시글 조회(동기화된 사용자): boardId={}", boardId);
        
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(ResponseCode.BOARD_NOT_FOUND));

        UserInfoDto userInfoDto = UserInfoDto.of(board.getUser().getEmail(), board.getUser().getName());

        return BoardResponseDto.from(board, userInfoDto);
    }

    @Transactional(readOnly = true)
    public PageResponse<BoardResponseDto> getBoards2(String keyword, SearchType searchType, Pageable pageable) {
        log.debug("게시글 검색: keyword={}, searchType={}, page={}", keyword, searchType, pageable.getPageNumber());
        
        Page<Board> boards;

        if (keyword == null || searchType == null) {
            boards = boardRepository.findAll(pageable);
        } else {
            if(SearchType.TITLE.equals(searchType)) boards = boardRepository.findByTitleContaining(keyword, pageable);
            else if(SearchType.CONTENT.equals(searchType)) boards = boardRepository.findByContentContaining(keyword, pageable);
            else if(SearchType.EMAIL.equals(searchType)) boards = boardRepository.findByUserEmailContaining(keyword, pageable);
            else boards = boardRepository.findByTitleOrContentContaining(keyword, pageable);
        }

        return PageResponse.of(
                boards.map(board ->
                        BoardResponseDto.from(
                                board,
                                UserInfoDto.of(
                                        board.getUser().getEmail(),
                                        board.getUser().getName()
                                )
                        )
                )
        );
    }

    @Transactional
    public void delete(Long boardId, Long userId) {
        log.info("게시글 삭제 시도: boardId={}, userId={}", boardId, userId);
        
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(ResponseCode.BOARD_NOT_FOUND));

        board.validateOwnership(userId);
        board.validateModifiable();

        boardRepository.delete(board);
        log.info("게시글 삭제 완료: boardId={}", boardId);
    }

    @Transactional
    public void update(Long boardId, UpdateBoardRequestDto dto, Long userId) {
        log.info("게시글 수정 시도: boardId={}, userId={}", boardId, userId);
        
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(ResponseCode.BOARD_NOT_FOUND));

        board.validateOwnership(userId);
        board.validateModifiable();

        board.update(dto.getTitle(), dto.getContent());
        log.info("게시글 수정 완료: boardId={}", boardId);
    }
}
