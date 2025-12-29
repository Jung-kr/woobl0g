package woobl0g.userservice.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

    @Schema(
            description = "이메일 (필수, 이메일 형식)",
            example = "test@example.com"
    )
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(
            description = "이름 (필수)",
            example = "홍길동"
    )
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @Schema(
            description = "비밀번호 (최소 8자 이상)",
            example = "password123",
            minLength = 8
    )
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;
}
