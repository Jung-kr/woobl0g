package woobl0g.pointservice.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.pointservice.point.domain.Point;

@Getter
@AllArgsConstructor
public class PointRankingResponseDto {

    private int rank;
    private int amount;
    private UserInfoDto userInfo;

    public static PointRankingResponseDto of(int rank, int amount, UserInfoDto userInfo) {
        return new PointRankingResponseDto(rank, amount, userInfo);
    }

}
