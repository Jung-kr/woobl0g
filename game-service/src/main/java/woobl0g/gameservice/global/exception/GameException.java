package woobl0g.gameservice.global.exception;

import woobl0g.gameservice.global.response.ResponseCode;

public class GameException extends BaseException {

    public GameException(ResponseCode code) {
        super(code);
    }
}
