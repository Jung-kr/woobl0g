package woobl0g.boardservice.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.boardservice.global.response.ResponseCode;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {

    private final ResponseCode code;

    @Override
    public String getMessage() {
        return code.getMessage();
    }
}
