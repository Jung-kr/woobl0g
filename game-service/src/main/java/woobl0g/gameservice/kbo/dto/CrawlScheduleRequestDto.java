package woobl0g.gameservice.kbo.dto;

import lombok.Getter;
import woobl0g.gameservice.kbo.domain.SeriesType;

@Getter
public class CrawlScheduleRequestDto {

    private Integer season;
    private Integer month;
    private SeriesType seriesType;

}
