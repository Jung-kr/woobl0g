package woobl0g.userservice.user.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import woobl0g.userservice.user.dto.AddPointsRequestDto;

@Slf4j
@Component
public class PointClient {

    private final RestClient restClient;

    public PointClient(@Value("${client.point-service.url}") String pointServiceUrl) {
        restClient = RestClient.builder()
                .baseUrl(pointServiceUrl)
                .build();
    }

    public void addPoints(Long userId, String actionType) {
        restClient.post()
                .uri("/internal/points/add")
                .contentType(MediaType.APPLICATION_JSON)
                .body(AddPointsRequestDto.of(userId, actionType))
                .retrieve()
                .toBodilessEntity();
    }
}
