package woobl0g.gameservice.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    // 200 OK
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "서비스가 정상적으로 동작 중입니다."),
    KBO_CRAWL_SUCCESS(HttpStatus.OK, "KBO 경기 일정 크롤링에 성공했습니다."),
    BET_PLACED_SUCCESS(HttpStatus.OK, "배팅이 성공적으로 완료되었습니다."),
    BET_CANCELLED_SUCCESS(HttpStatus.OK, "배팅이 취소되었습니다."),
    BET_SETTLEMENT_SUCCESS(HttpStatus.OK, "배팅 정산이 완료되었습니다."),
    BET_LIST_GET_SUCCESS(HttpStatus.OK, "배팅 내역 조회에 성공했습니다."),
    GAME_LIST_GET_SUCCESS(HttpStatus.OK, "경기 목록 조회에 성공했습니다."),
    GAME_DETAIL_GET_SUCCESS(HttpStatus.OK, "경기 상세 조회에 성공했습니다."),

    // 400 BAD REQUEST
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_BET_AMOUNT(HttpStatus.BAD_REQUEST, "배팅 금액은 100원 단위로 100원 이상 1,000원 이하만 가능합니다."),
    BETTING_NOT_OPEN(HttpStatus.BAD_REQUEST, "아직 배팅 오픈 시간이 아닙니다. 매일 낮 12시부터 배팅이 가능합니다."),
    BETTING_CLOSED(HttpStatus.BAD_REQUEST, "배팅이 마감되었습니다. 경기 시작 30분 전까지만 배팅 및 배팅 취소가 가능합니다."),
    CANNOT_CANCEL_BET(HttpStatus.BAD_REQUEST, "취소할 수 없는 배팅입니다. 이미 정산되었거나 취소된 배팅입니다."),
    DIFFERENT_BET_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "이미 다른 결과에 배팅하셨습니다. 같은 결과에만 추가 배팅이 가능합니다."),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),

    // 403 FORBIDDEN
    UNAUTHORIZED_BET_ACCESS(HttpStatus.FORBIDDEN, "본인의 배팅만 조회 및 취소할 수 있습니다."),

    // 404 NOT FOUND
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 경기입니다."),
    BET_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 배팅입니다."),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    KBO_CRAWLING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "KBO 경기 일정 크롤링 중 오류가 발생했습니다."),
    BET_SETTLEMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "배팅 정산 중 오류가 발생했습니다."),
    JSON_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 직렬화에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
