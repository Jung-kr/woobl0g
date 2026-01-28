package woobl0g.gameservice.bet.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import woobl0g.gameservice.global.exception.JsonConversionException;
import woobl0g.gameservice.global.response.ResponseCode;

@Getter
@AllArgsConstructor
public class BetCancelledEvent {

    private Long userId;
    private String actionType;
    private Integer amount;

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JacksonException e) {
            throw new JsonConversionException(ResponseCode.JSON_SERIALIZATION_FAILED);
        }
    }

    public static BetCancelledEvent of(Long userId, String actionType, Integer amount) {
        return new BetCancelledEvent(userId, actionType, amount);
    }
}
