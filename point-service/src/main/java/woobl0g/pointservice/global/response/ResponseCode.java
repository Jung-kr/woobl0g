package woobl0g.pointservice.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    // 400 BAD REQUEST
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    //200 OK
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "서비스가 정상적으로 동작 중입니다.");

    private final HttpStatus status;
    private final String message;
}
