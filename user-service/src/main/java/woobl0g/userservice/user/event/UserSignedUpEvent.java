package woobl0g.userservice.user.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woobl0g.userservice.global.exception.JsonConversionException;
import woobl0g.userservice.global.response.ResponseCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignedUpEvent {

    private Long userId;
    private String name;
    private String email;
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

    public static UserSignedUpEvent of(Long userId, String name, String email, String actionType, Integer amount) {
        return new UserSignedUpEvent(userId, name, email, actionType, amount);
    }
}
