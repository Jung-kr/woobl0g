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

    // 400 BAD REQUEST
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    KBO_CRAWLING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "KBO 경기 일정 크롤링 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
