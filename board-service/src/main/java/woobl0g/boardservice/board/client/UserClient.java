package woobl0g.boardservice.board.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import woobl0g.boardservice.board.dto.UserResponseDto;


@Slf4j
@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(@Value("${client.user-service.url}") String userServiceUrl) {
        restClient = RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    public UserResponseDto fetchUser(Long userId) {
        return restClient.get()
                    .uri("/api/users/{userId}", userId)
                    .retrieve()
                    .body(UserResponseDto.class);

    }
}
