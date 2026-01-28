package woobl0g.gameservice.bet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woobl0g.gameservice.game.domain.Game;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bets")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long betId;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Enumerated(EnumType.STRING)
    private BetType betType;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BetAction betAction;

    private LocalDateTime createdAt;

    @Builder
    private Bet(Long userId, Game game, BetType betType, Integer amount, BetAction betAction) {
        this.userId = userId;
        this.game = game;
        this.betType = betType;
        this.amount = amount;
        this.betAction = betAction;
        this.createdAt = LocalDateTime.now();
    }

    public static Bet create(Long userId, Game game, BetType betType, Integer amount, BetAction betAction) {
        return new Bet(userId, game, betType, amount, betAction);
    }
}
