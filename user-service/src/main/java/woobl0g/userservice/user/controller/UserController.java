package woobl0g.userservice.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import woobl0g.userservice.global.response.ApiResponse;
import woobl0g.userservice.user.dto.SignUpRequestDto;

@Tag(name = "User", description = "회원 관련 API")
public interface UserController {

    @Operation(
            summary = "회원가입",
            description = "이메일, 이름, 비밀번호를 입력받아 회원가입을 수행합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "⭕ 회원가입 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ 이메일 중복 or 입력값 유효성 검증 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SignUpRequestDto.class)
                    )
            )
            @Valid @RequestBody SignUpRequestDto dto
    );

}
