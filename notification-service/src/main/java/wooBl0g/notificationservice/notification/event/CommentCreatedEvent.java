package wooBl0g.notificationservice.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreatedEvent {

    private Long userId;
    private String actionType;

    public static CommentCreatedEvent fromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, CommentCreatedEvent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
