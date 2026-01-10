package wooBl0g.notificationservice.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignedUpEvent {

    private Long userId;
    private String name;
    private String email;
    private String actionType;

    public static UserSignedUpEvent fromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, UserSignedUpEvent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
