package woobl0g.boardservice.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(ResponseCode code) {
        return new ApiResponse<T>(true, code.getMessage(), null);
    }

    public static <T> ApiResponse<T> success(ResponseCode code, T data) {
        return new ApiResponse<T>(true, code.getMessage(), data);
    }

    public static <T> ApiResponse<T> fail(ResponseCode code) {
        return new ApiResponse<T>(false, code.getMessage(), null);
    }
}