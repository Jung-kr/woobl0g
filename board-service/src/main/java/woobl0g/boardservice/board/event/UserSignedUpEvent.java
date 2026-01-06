package woobl0g.boardservice.board.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woobl0g.boardservice.global.exception.JsonConversionException;
import woobl0g.boardservice.global.response.ResponseCode;

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
        } catch (JsonProcessingException e) {
            throw new JsonConversionException(ResponseCode.JSON_DESERIALIZATION_FAILED);
        }
    }
}
