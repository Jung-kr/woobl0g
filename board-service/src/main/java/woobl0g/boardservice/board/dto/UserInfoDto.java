package woobl0g.boardservice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {

    private String email;
    private String name;

    public static UserInfoDto of(String email, String name) {
        return new UserInfoDto(email, name);
    }
}
