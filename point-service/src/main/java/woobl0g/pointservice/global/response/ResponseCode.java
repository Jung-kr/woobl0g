package woobl0g.pointservice.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    // 200 OK
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "서비스가 정상적으로 동작 중입니다."),
    POINT_HISTORY_GET_SUCCESS(HttpStatus.OK, "포인트 이력 조회에 성공했습니다."),

    // 204 No Content
    POINT_DEDUCT_SUCCESS(HttpStatus.NO_CONTENT, "포인트 차감이 완료되었습니다."),
    POINT_ADD_SUCCESS(HttpStatus.NO_CONTENT, "포인트 적립이 완료되었습니다."),

    // 400 BAD REQUEST
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),

    // 404 NOT FOUND
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 포인트 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
