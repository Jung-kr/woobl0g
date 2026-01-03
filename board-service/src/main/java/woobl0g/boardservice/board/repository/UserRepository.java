package woobl0g.boardservice.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import woobl0g.boardservice.board.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
