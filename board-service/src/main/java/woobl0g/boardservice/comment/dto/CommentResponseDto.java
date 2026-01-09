package woobl0g.boardservice.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.boardservice.board.domain.User;
import woobl0g.boardservice.board.dto.UserInfoDto;
import woobl0g.boardservice.comment.domain.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private String content;
    private boolean isDeleted;
    private int depth;
    private UserInfoDto userInfo;
    private List<CommentResponseDto> replies;
    private LocalDateTime createdAt;

    public static CommentResponseDto from(Comment comment) {
        // UserInfoDto 생성
        UserInfoDto userInfoDto = null;
        if (!comment.isDeleted()) {
            User user = comment.getUser();
            userInfoDto = UserInfoDto.of(user.getEmail(), user.getName());
        }

        List<CommentResponseDto> replies = comment.getChildren().stream()
                .map(child -> {
                    UserInfoDto childUserDto = null;
                    if (!child.isDeleted()) {
                        User childUser = child.getUser();
                        childUserDto = UserInfoDto.of(childUser.getEmail(), childUser.getName());
                    }

                    return new CommentResponseDto(
                            child.getCommentId(),
                            child.getContent(),
                            child.isDeleted(),
                            child.getDepth(),
                            childUserDto,
                            new ArrayList<>(),
                            child.getCreatedAt()
                    );
                })
                .toList();

        return new CommentResponseDto(
                comment.getCommentId(),
                comment.getContent(),
                comment.isDeleted(),
                comment.getDepth(),
                userInfoDto,
                replies,
                comment.getCreatedAt()
        );
    }
}
