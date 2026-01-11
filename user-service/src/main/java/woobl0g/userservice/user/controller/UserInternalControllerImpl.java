package woobl0g.userservice.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.dto.UserResponseDto;
import woobl0g.userservice.user.service.UserService;

import java.util.List;

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

    @Override
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@RequestParam List<Long> userIds) {
        return ResponseEntity
                .status(ResponseCode.USER_GET_SUCCESS.getStatus())
                .body(userService.getUsers(userIds));
    }
}
