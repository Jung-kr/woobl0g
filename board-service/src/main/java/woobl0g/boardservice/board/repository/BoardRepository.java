package woobl0g.boardservice.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import woobl0g.boardservice.board.domain.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board,Long> {

    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.title LIKE %:keyword%")
    Page<Board> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.content LIKE %:keyword%")
    Page<Board> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<Board> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b JOIN FETCH b.user u WHERE u.email LIKE %:keyword%")
    Page<Board> findByUserEmailContaining(@Param("keyword") String keyword, Pageable pageable);
}
