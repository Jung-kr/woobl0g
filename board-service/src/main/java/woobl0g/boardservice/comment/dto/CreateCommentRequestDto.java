package woobl0g.boardservice.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateCommentRequestDto {

    String content;
    Long parentId;
}
