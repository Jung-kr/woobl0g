package woobl0g.pointservice.global.exception;

import woobl0g.pointservice.global.response.ResponseCode;

public class JsonConversionException extends BaseException {

    public JsonConversionException(ResponseCode responseCode) {
        super(responseCode);
    }
}
