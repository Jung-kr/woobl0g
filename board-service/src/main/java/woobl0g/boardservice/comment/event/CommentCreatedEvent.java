package woobl0g.boardservice.comment.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import woobl0g.boardservice.global.exception.JsonConversionException;
import woobl0g.boardservice.global.response.ResponseCode;

@Getter
@AllArgsConstructor
public class CommentCreatedEvent {

    private Long userId;
    private String actionType;

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new JsonConversionException(ResponseCode.JSON_SERIALIZATION_FAILED);
        }
    }
}
