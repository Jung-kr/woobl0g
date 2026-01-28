package wooBl0g.notificationservice.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BetSettledEvent {

    private Long userId;
    private String actionType;
    private Integer amount;

    public static BetSettledEvent fromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, BetSettledEvent.class);
        } catch (JacksonException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}

