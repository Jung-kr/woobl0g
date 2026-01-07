package woobl0g.boardservice.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    // 400 BAD REQUEST
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    REPLY_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, "대댓글에는 답글을 달 수 없습니다."),
    BOARD_MODIFY_TOO_EARLY(HttpStatus.BAD_REQUEST, "게시글은 작성 후 1일이 지나야 수정 or 삭제할 수 있습니다."),
    COMMENT_MODIFY_TOO_EARLY(HttpStatus.BAD_REQUEST, "댓글은 작성 후 1일이 지나야 수정 or 삭제할 수 있습니다."),

    // 403 FORBIDDEN
    COMMENT_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "댓글을 삭제할 권한이 없습니다."),
    COMMENT_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "댓글을 수정할 권한이 없습니다."),
    BOARD_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "게시글을 삭제할 권한이 없습니다."),
    BOARD_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "게시글을 수정할 권한이 없습니다."),

    // 404 NOT FOUND
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    //200 OK,
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "서비스가 정상적으로 동작 중입니다."),
    COMMENT_GET_SUCCESS(HttpStatus.OK, "댓글 조회에 성공했습니다."),
    COMMENT_DELETE_SUCCESS(HttpStatus.OK, "댓글 삭제에 성공했습니다."),
    COMMENT_UPDATE_SUCCESS(HttpStatus.OK, "댓글 수정에 성공했습니다."),
    BOARD_GET_SUCCESS(HttpStatus.OK, "게시글 조회에 성공했습니다."),
    BOARD_DELETE_SUCCESS(HttpStatus.OK, "게시글 삭제에 성공했습니다."),
    BOARD_UPDATE_SUCCESS(HttpStatus.OK, "게시글 수정에 성공했습니다."),

    //201 CREATED
    BOARD_CREATED(HttpStatus.CREATED, "게시글이 생성되었습니다."),
    COMMENT_CREATED(HttpStatus.CREATED, "댓글이 생성되었습니다."),

    // 500 INTERNAL SERVER ERROR
    BOARD_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 생성에 실패했습니다."),
    JSON_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 직렬화에 실패했습니다."),
    JSON_DESERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 역직렬화에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
