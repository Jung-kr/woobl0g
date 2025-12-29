package woobl0g.userservice.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woobl0g.userservice.global.response.ApiResponse;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.dto.SignUpRequestDto;
import woobl0g.userservice.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpRequestDto dto) {
        userService.signUp(dto);
        return ResponseEntity
                .status(ResponseCode.SIGN_UP_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.SIGN_UP_SUCCESS));
    }
}
