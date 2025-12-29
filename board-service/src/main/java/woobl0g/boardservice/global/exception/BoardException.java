package woobl0g.boardservice.global.exception;

import woobl0g.boardservice.global.response.ResponseCode;

public class BoardException extends BaseException {

    public BoardException(ResponseCode responseCode) {
        super(responseCode);
    }
}
