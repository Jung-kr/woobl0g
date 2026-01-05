package woobl0g.boardservice.global.exception;

import woobl0g.boardservice.global.response.ResponseCode;

public class CommentException extends BaseException {

    public CommentException(ResponseCode responseCode) {
        super(responseCode);
    }
}
