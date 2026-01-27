package woobl0g.gameservice.kbo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CancellationReason {

    RAIN("우천"),
    GROUND("구장"),
    COVID("코로나"),
    OTHER("기타");

    private final String description;

    /**
     * 문자열로부터 CancellationReason 찾기
     * '-'이거나 null이면 null 반환
     */
    public static CancellationReason fromString(String text) {
        if (text == null || text.trim().isEmpty() || "-".equals(text.trim())) {
            return null;
        }

        return Arrays.stream(CancellationReason.values())
                .filter(reason -> text.contains(reason.description))
                .findFirst()
                .orElse(null);
    }
}
