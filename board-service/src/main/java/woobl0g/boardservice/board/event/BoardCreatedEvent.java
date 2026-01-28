package woobl0g.boardservice.board.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.fasterxml.jackson.databind.ObjectMapper;
import woobl0g.boardservice.global.exception.JsonConversionException;
import woobl0g.boardservice.global.response.ResponseCode;

@Getter
@AllArgsConstructor
public class BoardCreatedEvent {

    private Long userId;
    private String actionType;
    private Integer amount;

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new JsonConversionException(ResponseCode.JSON_SERIALIZATION_FAILED);
        }
    }

    public static BoardCreatedEvent of(Long userId, String actionType, Integer amount) {
        return new BoardCreatedEvent(userId, actionType, amount);
    }
}
