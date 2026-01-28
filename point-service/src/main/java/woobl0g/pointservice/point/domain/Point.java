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

    public static Point create(Long userId) {
        return new Point(userId, 0);
    }

    // 포인트 적립
    public PointHistory addAmount(PointActionType actionType, Integer amount) {
        this.amount += amount;
        return PointHistory.create(userId, amount, actionType.name());
    }

    // 포인트 차감
    public PointHistory deductAmount(PointActionType actionType, Integer amount) {
        if (this.amount < amount) {
            throw new PointException(ResponseCode.INSUFFICIENT_POINT);
        }
        this.amount -= amount;

        return PointHistory.create(userId, -amount, actionType.name());
    }
}
