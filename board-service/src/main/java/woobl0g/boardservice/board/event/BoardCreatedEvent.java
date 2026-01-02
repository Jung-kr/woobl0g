package woobl0g.boardservice.board.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.fasterxml.jackson.databind.ObjectMapper;
import woobl0g.boardservice.global.exception.JsonSerializationException;
import woobl0g.boardservice.global.response.ResponseCode;

@Getter
@AllArgsConstructor
public class BoardCreatedEvent {

    private Long userId;
    private String actionType;

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException(ResponseCode.JSON_SERIALIZATION_FAILED);
        }
    }
}
