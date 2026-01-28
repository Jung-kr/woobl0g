package woobl0g.pointservice.point.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woobl0g.pointservice.global.exception.JsonConversionException;
import woobl0g.pointservice.global.response.ResponseCode;

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
        } catch (JsonProcessingException e) {
            throw new JsonConversionException(ResponseCode.JSON_DESERIALIZATION_FAILED);
        }
    }
}
