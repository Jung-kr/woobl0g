package woobl0g.pointservice.point.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import woobl0g.pointservice.point.domain.FailureStatus;
import woobl0g.pointservice.point.domain.PointFailureHistory;

import java.util.List;

@Repository
public interface PointFailureHistoryRepository extends JpaRepository<PointFailureHistory, Long> {

    List<PointFailureHistory> findAllByStatus(FailureStatus status, Pageable pageable);
}
