package woobl0g.boardservice.global.exception;

import woobl0g.boardservice.global.response.ResponseCode;

public class UserException extends BaseException {

    public UserException(ResponseCode responseCode) {
        super(responseCode);
    }
}
