package woobl0g.userservice.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import woobl0g.userservice.user.dto.UserResponseDto;

@Tag(name = "User", description = "회원 관련 내부용 API")
public interface UserInternalController {

    ResponseEntity<UserResponseDto> getUser(@PathVariable Long userId);
}
