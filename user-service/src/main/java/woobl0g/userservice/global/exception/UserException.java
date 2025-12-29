package woobl0g.userservice.global.exception;

import woobl0g.userservice.global.response.ResponseCode;

public class UserException extends BaseException {

    public UserException(ResponseCode responseCode) {
        super(responseCode);
    }
}
