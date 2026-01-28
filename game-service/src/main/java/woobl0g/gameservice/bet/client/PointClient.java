package woobl0g.gameservice.bet.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import woobl0g.gameservice.bet.dto.AddPointsRequestDto;
import woobl0g.gameservice.bet.dto.DeductPointsRequestDto;
import woobl0g.gameservice.global.exception.BetException;
import woobl0g.gameservice.global.response.ResponseCode;

@Slf4j
@Component
public class PointClient {

    private final RestClient restClient;

    public PointClient(@Value("${client.point-service.url}") String pointServiceUrl) {
        restClient = RestClient.builder()
                .baseUrl(pointServiceUrl)
                .build();
    }

    public void addPoints(Long userId, String actionType, Integer amount) {
        restClient.post()
                .uri("/internal/points/add")
                .contentType(MediaType.APPLICATION_JSON)
                .body(AddPointsRequestDto.of(userId, actionType, amount))
                .retrieve()
                .toBodilessEntity();
    }

    public void deductPoints(Long userId, String actionType, Integer amount) {
        this.restClient.post()
                .uri("/internal/points/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .body(DeductPointsRequestDto.of(userId, actionType, amount))
                .retrieve()
                .onStatus(status -> status.value() == 400, ((request, response) -> {
                    throw new BetException(ResponseCode.INSUFFICIENT_POINT);
                }))
                .toBodilessEntity();
    }
}
