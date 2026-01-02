package woobl0g.boardservice.board.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.boardservice.board.client.PointClient;
import woobl0g.boardservice.board.client.UserClient;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.dto.BoardResponseDto;
import woobl0g.boardservice.board.dto.CreateBoardRequestDto;
import woobl0g.boardservice.board.dto.UserInfoDto;
import woobl0g.boardservice.board.dto.UserResponseDto;
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
    private final PointClient pointClient;
    private final BoardRepository boardRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void create(CreateBoardRequestDto dto) {

        // 게시글 등록 성공 여부를 판단하는 플래그
        boolean isBoardCreated = false;
        Long savedBoardId = null;

        // 포인트 차감 성공 여부를 판단하는 플래그
        boolean isPointDeducted = false;

        try {
            // 게시글 작성 전 10 포인트 차감
            pointClient.deductPoints(dto.getUserId(), "BOARD_CREATE");
            isPointDeducted = true;
            log.info("[게시글 생성] 포인트 차감 성공 - userId = {}", dto.getUserId());

            // 게시글 작성
            Board board = Board.create(dto.getTitle(), dto.getContent(), dto.getUserId());
            savedBoardId = boardRepository.save(board).getBoardId();
            isBoardCreated = true;
            log.info("[게시글 생성] 게시글 저장 성공 - boardId = {}", savedBoardId);

            // 게시글 작성 시 활동 점수 10점 부여
//            userClient.addActivityScore(dto.getUserId(), "BOARD_CREATE");

            BoardCreatedEvent boardCreatedEvent = new BoardCreatedEvent(dto.getUserId());
            kafkaTemplate.send("board.created", boardCreatedEvent);
            log.info("[게시글 생성] 활동 점수 적립 성공 - userId = {}", savedBoardId);
        } catch (Exception e) {
            log.error("[게시글 생성 실패] - userId = {}", savedBoardId, e);
            if(isBoardCreated) {
                // 게시글 작성 보상 트랜잭션 -> 게시글 삭제
                boardRepository.deleteById(savedBoardId);
                log.warn("[보상 트랜잭션] 게시글 삭제 성공 - boardId = {}", savedBoardId);
            }
            if(isPointDeducted) {
                // 포인트 차감 보상 트랜잭션 -> 포인트 적립
                pointClient.addPoints(dto.getUserId(), "BOARD_CREATE");
                log.warn("[보상 트랜잭션] 포인트 적립 성공 - userId = {}", savedBoardId);
            }

            throw new BoardException(ResponseCode.BOARD_CREATE_FAILED);
        }
    }

//    // 객체를 Json 형태의 String으로 만들어주는 메서드
//    // (클래스로 분리하면 더 좋지만 편의를 위해 메서드로만 분리)
//    private String toJsonString(Object object) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String message = objectMapper.writeValueAsString(object);
//            return message;
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Json 직렬화 실패");
//        }
//    }


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
