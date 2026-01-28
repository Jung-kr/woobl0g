package woobl0g.gameservice.global.exception;

import woobl0g.gameservice.global.response.ResponseCode;

public class BetException extends BaseException{

    public BetException(ResponseCode code) {
        super(code);
    }
}
