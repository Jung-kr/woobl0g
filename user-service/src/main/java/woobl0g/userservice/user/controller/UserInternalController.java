package woobl0g.userservice.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import woobl0g.userservice.user.dto.UserResponseDto;

import java.util.List;

@Tag(name = "User [Internal]", description = "회원 관련 내부용 API")
public interface UserInternalController {

    @Operation(
            summary = "단일 사용자 조회",
            description = "사용자 ID를 사용하여 단일 사용자 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 사용자 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDto.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "❌ 사용자를 찾을 수 없습니다",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    ResponseEntity<UserResponseDto> getUser(
            @Parameter(description = "조회할 사용자 ID", required = true, example = "1")
            @PathVariable Long userId
    );

    @Operation(
            summary = "다중 사용자 조회",
            description = "사용자 ID 목록을 사용하여 여러 사용자 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "⭕ 사용자 목록 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class))
                    )
            )
    })
    ResponseEntity<List<UserResponseDto>> getAllUsers(
            @Parameter(
                    description = "조회할 사용자 ID 목록",
                    required = true,
                    example = "[1, 2, 3]"
            )
            @RequestParam List<Long> userIds
    );
}

