package woobl0g.userservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.userservice.user.domain.User;

@Getter
@AllArgsConstructor
public class UserResponseDto {

    private String email;
    private String name;

    public static UserResponseDto from(User user) {
        return new UserResponseDto(user.getEmail(),  user.getName());
    }
}
