package woobl0g.gameservice.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpsertGameResponseDto {

    private Integer collectedCount;
    private Integer savedCount;
    private Integer modifiedCount;
}
