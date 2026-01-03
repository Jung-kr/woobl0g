package woobl0g.userservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.userservice.user.domain.User;

@Getter
@AllArgsConstructor
public class UserRankingResponseDto {

    private int rank;
    private String name;
    private String email;
    private int activityScore;

    public static UserRankingResponseDto of(int rank, User user) {
        return new UserRankingResponseDto(
                rank,
                user.getName(),
                user.getEmail(),
                user.getActivityScore()
        );
    }
}
