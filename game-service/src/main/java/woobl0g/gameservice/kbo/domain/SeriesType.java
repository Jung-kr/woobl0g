package woobl0g.gameservice.kbo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeriesType {

    PRESEASON("1", "시범경기"),
    REGULAR_SEASON("0,9,6", "정규시즌"),
    POSTSEASON("3,4,5,7", "포스트시즌");

    private final String value;
    private final String description;
}
