package woobl0g.userservice.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.dto.UserResponseDto;
import woobl0g.userservice.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/users")
public class UserInternalControllerImpl implements UserInternalController {

    private final UserService userService;

    @Override
    @GetMapping("{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long userId) {
        return ResponseEntity
                .status(ResponseCode.USER_GET_SUCCESS.getStatus())
                .body(userService.getUser(userId));
    }
}
