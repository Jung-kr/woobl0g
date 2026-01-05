package woobl0g.boardservice.comment.dto;

import lombok.Getter;

@Getter
public class CreateCommentRequestDto {

    String content;
    Long parentId;
}
