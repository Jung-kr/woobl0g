package woobl0g.gameservice.kbo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Team {

    DOOSAN("두산", "OB"),
    LG("LG", "LG"),
    KT("KT", "KT"),
    SSG("SSG", "SK"),
    NC("NC", "NC"),
    KIWOOM("키움", "히어로즈"),
    SAMSUNG("삼성", "삼성"),
    LOTTE("롯데", "롯데"),
    HANWHA("한화", "한화"),
    KIA("KIA", "해태");

    private final String teamName;
    private final String alternativeName;

    /**
     * 팀 이름으로 Team Enum 찾기
     */
    public static Team findByTeamName(String name) {
        return Arrays.stream(Team.values())
                .filter(team -> team.teamName.equals(name) || team.alternativeName.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown team: " + name));
    }
}
