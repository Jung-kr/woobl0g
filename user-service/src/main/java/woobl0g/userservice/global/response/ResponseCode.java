package woobl0g.userservice.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    // 400 BAD REQUEST
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),

    //200 OK
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "서비스가 정상적으로 동작 중입니다."),

    // 201 CREATED
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다.");

    private final HttpStatus status;
    private final String message;
}
