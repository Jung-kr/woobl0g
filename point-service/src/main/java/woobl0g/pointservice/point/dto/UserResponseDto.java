package woobl0g.pointservice.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {

    private Long userId;
    private String email;
    private String name;
}
