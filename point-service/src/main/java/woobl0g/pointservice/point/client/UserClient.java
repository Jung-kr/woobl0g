package woobl0g.pointservice.point.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import woobl0g.pointservice.point.dto.UserResponseDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(@Value("${client.user-service.url}") String userServiceUrl) {
        restClient = RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    public Optional<UserResponseDto> fetchUser(Long userId) {
        try {
            UserResponseDto userResponseDto = restClient.get()
                    .uri("/internal/users/{userId}", userId)
                    .retrieve()
                    .body(UserResponseDto.class);
            return Optional.ofNullable(userResponseDto);
        } catch (RestClientException e) {
            log.error("사용자 정보 조회 실패: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public List<UserResponseDto> fetchUsers(List<Long> userIds) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/internal/users")
                            .queryParam("userIds", userIds)
                            .build()
                    )
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientException e) {
            log.error("사용자 정보 조회 실패: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}


