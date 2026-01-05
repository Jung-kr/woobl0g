package woobl0g.boardservice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BoardResponseDto {

    private Long BoardId;
    private String title;
    private String content;
    private UserInfoDto user;
    private LocalDateTime createdAt;
}
