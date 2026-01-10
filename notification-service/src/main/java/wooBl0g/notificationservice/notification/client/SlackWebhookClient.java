package wooBl0g.notificationservice.notification.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class SlackWebhookClient {

    private final String webhookUrl;
    private final RestClient restClient;

    public SlackWebhookClient(
            @Value("${slack.webhook-url}") String webhookUrl
    ) {
        this.webhookUrl = webhookUrl;
        this.restClient = RestClient.builder().build();
    }

    public void sendMessage(String message) {
        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("text", message))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Slack Webhook 전송 실패", e);
        }
    }
}
