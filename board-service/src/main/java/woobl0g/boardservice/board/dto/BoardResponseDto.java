package woobl0g.boardservice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.boardservice.board.domain.Board;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BoardResponseDto {

    private Long boarId;
    private String title;
    private String content;
    private UserInfoDto userInfo;
    private LocalDateTime createdAt;

    public static BoardResponseDto from(Board board, UserInfoDto userInfo) {
        return new BoardResponseDto(
                board.getBoardId(),
                board.getTitle(),
                board.getContent(),
                userInfo,
                board.getCreatedAt()
        );
    }
}
