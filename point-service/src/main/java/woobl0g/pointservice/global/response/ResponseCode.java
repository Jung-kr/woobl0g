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
    POINT_RANKING_GET_SUCCESS(HttpStatus.OK, "포인트 랭킹 조회에 성공했습니다."),
    ADMIN_POINT_FAILURE_GET_SUCCESS(HttpStatus.OK, "[관리자] 포인트 적립 실패 내역 조회에 성공했습니다."),
    ADMIN_POINT_FAILURE_RETRY_SUCCESS(HttpStatus.OK, "[관리자] 포인트 적립 실패에 대한 재시도가 완료되었습니다."),
    ADMIN_POINT_FAILURE_IGNORE_SUCCESS(HttpStatus.OK, "[관리자] 포인트 적립 실패에 대한 무시 처리가 완료되었습니다."),


    // 204 No Content
    POINT_DEDUCT_SUCCESS(HttpStatus.NO_CONTENT, "포인트 차감이 완료되었습니다."),
    POINT_ADD_SUCCESS(HttpStatus.NO_CONTENT, "포인트 적립이 완료되었습니다."),

    // 400 BAD REQUEST
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),
    ADMIN_POINT_FAILURE_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "[관리자] 이미 처리된 포인트 적립 실패 내역입니다."),

    // 404 NOT FOUND
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 포인트 정보를 찾을 수 없습니다."),
    ADMIN_POINT_FAILURE_NOT_FOUND(HttpStatus.NOT_FOUND, "[관리자] 포인트 적립 실패 내역을 찾을 수 없습니다."),


    // 500 INTERNAL SERVER ERROR
    JSON_DESERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 역직렬화에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
