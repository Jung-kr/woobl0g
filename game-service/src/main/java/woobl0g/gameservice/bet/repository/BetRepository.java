package woobl0g.gameservice.bet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import woobl0g.gameservice.bet.domain.Bet;
import woobl0g.gameservice.bet.domain.BetAction;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

    List<Bet> findByUserIdAndGame_GameIdAndBetStatus(Long userId, Long gameGameId, BetAction betAction);

    List<Bet> findByUserId(Long userId, Pageable pageable);
}