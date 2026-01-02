package woobl0g.userservice.global.exception;

import woobl0g.userservice.global.response.ResponseCode;

public class JsonDeserializationException extends BaseException {

    public JsonDeserializationException(ResponseCode responseCode) {
        super(responseCode);
    }
}
