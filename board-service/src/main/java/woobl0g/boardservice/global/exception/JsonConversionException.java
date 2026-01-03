package woobl0g.boardservice.global.exception;

import woobl0g.boardservice.global.response.ResponseCode;

public class JsonConversionException extends BaseException {

    public JsonConversionException(ResponseCode responseCode) {
        super(responseCode);
    }
}
