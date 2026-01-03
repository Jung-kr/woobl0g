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

    // 404 NOT FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    //200 OK
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "서비스가 정상적으로 동작 중입니다."),
    USER_GET_SUCCESS(HttpStatus.OK, "사용자 조회에 성공했습니다."),
    ACTIVITY_SCORE_HISTORY_GET_SUCCESS(HttpStatus.OK, "활동 점수 이력 조회에 성공했습니다."),
    RANKING_GET_SUCCESS(HttpStatus.OK, "랭킹 조회에 성공했습니다."),
    SCORE_ADD_SUCCESS(HttpStatus.NO_CONTENT, "활동 점수 적립이 완료되었습니다."),

    // 201 CREATED
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),

    // 500 INTERNAL SERVER ERROR
    JSON_DESERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 역직렬화에 실패했습니다."),
    JSON_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 직렬화에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
