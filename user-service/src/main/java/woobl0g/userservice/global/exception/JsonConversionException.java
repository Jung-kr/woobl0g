package woobl0g.userservice.global.exception;

import woobl0g.userservice.global.response.ResponseCode;

public class JsonConversionException extends BaseException {

    public JsonConversionException(ResponseCode responseCode) {
        super(responseCode);
    }
}
