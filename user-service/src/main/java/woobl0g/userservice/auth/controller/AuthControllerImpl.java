package woobl0g.userservice.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woobl0g.userservice.auth.dto.LoginRequestDto;
import woobl0g.userservice.auth.dto.RefreshTokenRequestDto;
import woobl0g.userservice.auth.dto.SignUpRequestDto;
import woobl0g.userservice.auth.dto.TokenResponseDto;
import woobl0g.userservice.auth.service.AuthService;
import woobl0g.userservice.global.response.ApiResponse;
import woobl0g.userservice.global.response.ResponseCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthControllerImpl {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignUpRequestDto dto) {
        authService.signUp(dto);
        return ResponseEntity
                .status(ResponseCode.SIGN_UP_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.SIGN_UP_SUCCESS));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDto>> login(@RequestBody LoginRequestDto dto) {
        return ResponseEntity
                .status(ResponseCode.LOGIN_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.LOGIN_SUCCESS, authService.login(dto)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponseDto>> refresh(@RequestBody RefreshTokenRequestDto dto) {
        return ResponseEntity
                .status(ResponseCode.TOKEN_REFRESH_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.TOKEN_REFRESH_SUCCESS, authService.getRefreshToken(dto)));
    }
}
