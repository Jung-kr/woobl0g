package woobl0g.pointservice.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.pointservice.point.domain.Point;

@Getter
@AllArgsConstructor
public class PointRankingResponseDto {

    private int rank;
    private Long userId;
    private int amount;

    public static PointRankingResponseDto of(int rank, Point point) {
        return new PointRankingResponseDto(rank, point.getUserId(), point.getAmount());
    }

}
