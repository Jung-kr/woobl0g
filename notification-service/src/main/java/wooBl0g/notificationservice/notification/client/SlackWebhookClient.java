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
        log.debug("Slack Webhook 호출 시작");
        
        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("text", message))
                    .retrieve()
                    .toBodilessEntity();
            
            log.debug("Slack Webhook 호출 성공");
        } catch (Exception e) {
            log.error("Slack Webhook 전송 실패", e);
        }
    }
}
