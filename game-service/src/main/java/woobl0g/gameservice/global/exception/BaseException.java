package woobl0g.gameservice.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.gameservice.global.response.ResponseCode;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {

    private final ResponseCode code;

    @Override
    public String getMessage() {
        return code.getMessage();
    }
}
