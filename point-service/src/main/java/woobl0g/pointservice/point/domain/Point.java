package woobl0g.pointservice.point.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woobl0g.pointservice.global.exception.PointException;
import woobl0g.pointservice.global.response.ResponseCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "points")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;
    private Long userId;
    private int amount;

    private Point(Long userId, Integer amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public static Point create(Long userId, int amount) {
        return new Point(userId, amount);
    }

    // 포인트 적립
    public void addAmount(int amount) {
        this.amount += amount;
    }

    // 포인트 차감
    public void deductAmount(int amount) {
        if (this.amount < amount) {
            throw new PointException(ResponseCode.INSUFFICIENT_POINT);
        }
        this.amount -= amount;
    }
}
