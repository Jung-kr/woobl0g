package woobl0g.pointservice.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import woobl0g.pointservice.point.domain.PointHistory;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    List<PointHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
