package woobl0g.boardservice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SavedUserRequestDto {

    private Long userId;
    private String name;
    private String email;

    public static SavedUserRequestDto of(Long userId, String name, String email) {
        return new SavedUserRequestDto(userId, name, email);
    }
}
