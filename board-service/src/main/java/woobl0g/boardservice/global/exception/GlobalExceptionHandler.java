package woobl0g.boardservice.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import woobl0g.boardservice.global.response.ApiResponse;
import woobl0g.boardservice.global.response.ResponseCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
        log.warn("비즈니스 예외 발생 - code: {}, message: {}", e.getCode(), e.getMessage());
        
        return ResponseEntity
                .status(e.getCode().getStatus())
                .body(ApiResponse.fail(e.getCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        log.warn("검증 실패 - field: {}, message: {}", 
                e.getBindingResult().getFieldErrors().get(0).getField(), message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ResponseCode.INVALID_REQUEST, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception e) {
        log.error("예상치 못한 예외 발생", e);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ResponseCode.INTERNAL_SERVER_ERROR));
    }
}
