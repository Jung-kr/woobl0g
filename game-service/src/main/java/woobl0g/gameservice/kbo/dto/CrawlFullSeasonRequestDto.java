package woobl0g.gameservice.kbo.dto;

import lombok.Getter;
import woobl0g.gameservice.kbo.domain.SeriesType;

@Getter
public class CrawlFullSeasonRequestDto {

    private Integer season;
    private SeriesType seriesType;

}
