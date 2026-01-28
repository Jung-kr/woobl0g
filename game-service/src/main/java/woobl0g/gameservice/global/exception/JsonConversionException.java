package woobl0g.gameservice.global.exception;

import woobl0g.gameservice.global.response.ResponseCode;

public class JsonConversionException extends BaseException {

    public JsonConversionException(ResponseCode code) {
        super(code);
    }
}
