package wooBl0g.notificationservice.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreatedEvent {

    private Long userId;
    private String actionType;

    public static BoardCreatedEvent fromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, BoardCreatedEvent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}