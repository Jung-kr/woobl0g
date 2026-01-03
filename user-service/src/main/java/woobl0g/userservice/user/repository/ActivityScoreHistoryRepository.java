package woobl0g.userservice.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import woobl0g.userservice.user.domain.ActivityScoreHistory;

import java.util.List;

@Repository
public interface ActivityScoreHistoryRepository extends JpaRepository<ActivityScoreHistory, Long> {

    List<ActivityScoreHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
