package woobl0g.userservice.user.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woobl0g.userservice.global.exception.JsonDeserializationException;
import woobl0g.userservice.global.response.ResponseCode;

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
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationException(ResponseCode.JSON_DESERIALIZATION_FAILED);
        }
    }
}
