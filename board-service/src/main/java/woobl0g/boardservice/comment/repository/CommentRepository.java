package woobl0g.boardservice.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import woobl0g.boardservice.comment.domain.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    //batch size (application.yml에 배치 설정)
    List<Comment> findByBoard_BoardIdAndParentIsNull(Long boardId);

    //fetch join
    @Query("SELECT DISTINCT c FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH c.children ch " +
            "LEFT JOIN FETCH ch.user " +
            "WHERE c.board.boardId = :boardId AND c.parent IS NULL " +
            "ORDER BY c.createdAt DESC"
    )
    List<Comment> findByBoardIdWithReplies(@Param("boardId") Long boardId);
}
