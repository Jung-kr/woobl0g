package woobl0g.pointservice.global.exception;

import woobl0g.pointservice.global.response.ResponseCode;

public class PointException extends BaseException {

    public PointException(ResponseCode responseCode) {
        super(responseCode);
    }
}
