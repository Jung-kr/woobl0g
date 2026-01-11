package woobl0g.boardservice.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.domain.User;
import woobl0g.boardservice.board.dto.UpdateCommentRequestDto;
import woobl0g.boardservice.board.repository.BoardRepository;
import woobl0g.boardservice.board.repository.UserRepository;
import woobl0g.boardservice.comment.domain.Comment;
import woobl0g.boardservice.comment.dto.CommentResponseDto;
import woobl0g.boardservice.comment.dto.CreateCommentRequestDto;
import woobl0g.boardservice.comment.event.CommentCreatedEvent;
import woobl0g.boardservice.comment.repository.CommentRepository;
import woobl0g.boardservice.global.exception.BoardException;
import woobl0g.boardservice.global.exception.CommentException;
import woobl0g.boardservice.global.exception.UserException;
import woobl0g.boardservice.global.response.ResponseCode;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void create(Long boardId, CreateCommentRequestDto dto, Long userId) {
        log.info("댓글 생성 시작: boardId={}, userId={}, parentId={}", boardId, userId, dto.getParentId());
        
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

        CommentCreatedEvent commentCreatedEvent = new CommentCreatedEvent(userId, "COMMENT_CREATE");
        kafkaTemplate.send("comment.created", commentCreatedEvent.toJson());
        
        log.info("댓글 생성 완료 및 이벤트 발행: commentId={}, userId={}", comment.getCommentId(), userId);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long boardId) {
        log.debug("댓글 목록 조회: boardId={}", boardId);
        
        return commentRepository.findByBoardIdWithReplies(boardId).stream()
                .map(CommentResponseDto::from)
                .toList();
    }

    @Transactional
    public void delete(Long commentId, Long userId) {
        log.info("댓글 삭제 시도: commentId={}, userId={}", commentId, userId);
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ResponseCode.COMMENT_NOT_FOUND));

        comment.validateOwnership(userId);
        comment.validateModifiable();

        if (comment.shouldSoftDelete()) {
            comment.softDelete();
            log.info("댓글 소프트 삭제 완료: commentId={}", commentId);
        } else {
            commentRepository.delete(comment);
            log.info("댓글 삭제 완료: commentId={}", commentId);
        }
    }

    @Transactional
    public void update(Long commentId, UpdateCommentRequestDto dto, Long userId) {
        log.info("댓글 수정 시도: commentId={}, userId={}", commentId, userId);
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ResponseCode.COMMENT_NOT_FOUND));

        comment.validateOwnership(userId);
        comment.validateModifiable();

        comment.update(dto.getContent());
        log.info("댓글 수정 완료: commentId={}", commentId);
    }
}
