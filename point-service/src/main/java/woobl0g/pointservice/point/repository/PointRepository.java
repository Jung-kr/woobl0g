package woobl0g.pointservice.point.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import woobl0g.pointservice.point.domain.Point;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<Point> findAllByOrderByAmountDesc(Pageable pageable);
}
