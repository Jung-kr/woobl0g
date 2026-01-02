package woobl0g.boardservice.global.exception;

import woobl0g.boardservice.global.response.ResponseCode;

public class JsonSerializationException extends BaseException {

    public JsonSerializationException(ResponseCode responseCode) {
        super(responseCode);
    }
}
