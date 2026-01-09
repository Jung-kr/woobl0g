package woobl0g.userservice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType;

    public static TokenResponseDto of(String accessToken, String refreshToken, String tokenType) {
        return new TokenResponseDto(accessToken, refreshToken, tokenType);
    }
}
