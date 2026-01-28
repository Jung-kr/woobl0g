package woobl0g.gameservice.game.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import woobl0g.gameservice.game.domain.Game;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game,Long> {

    List<Game> findByGameKeyIn(List<String> gameKeys);

    List<Game> findByDate(LocalDate date, Pageable pageable);
}
