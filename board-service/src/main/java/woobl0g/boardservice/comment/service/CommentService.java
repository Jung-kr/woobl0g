package woobl0g.boardservice.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.domain.User;
import woobl0g.boardservice.board.repository.BoardRepository;
import woobl0g.boardservice.board.repository.UserRepository;
import woobl0g.boardservice.comment.domain.Comment;
import woobl0g.boardservice.comment.dto.CommentResponseDto;
import woobl0g.boardservice.comment.dto.CreateCommentRequestDto;
import woobl0g.boardservice.comment.repository.CommentRepository;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.exception.CommentException;
import woobl0g.boardservice.global.exception.UserException;
import woobl0g.boardservice.global.response.ResponseCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void create(Long boardId, CreateCommentRequestDto dto, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(ResponseCode.BOARD_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ResponseCode.USER_NOT_FOUND));

        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new CommentException(ResponseCode.COMMENT_NOT_FOUND));
        }

        Comment comment = Comment.create(dto.getContent(), board, user, parent);
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long boardId) {
        return commentRepository.findByBoardIdWithReplies(boardId).stream()
                .map(CommentResponseDto::from)
                .toList();
    }

    @Transactional
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ResponseCode.COMMENT_NOT_FOUND));

        if(!comment.getUser().getUserId().equals(userId)) {
            throw new CommentException(ResponseCode.COMMENT_DELETE_FORBIDDEN);
        }

        if (!comment.getChildren().isEmpty()) {
            comment.softDelete();
        } else {
            commentRepository.delete(comment);
        }
    }
}
