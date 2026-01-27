package woobl0g.gameservice.kbo.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import woobl0g.gameservice.kbo.domain.*;
import woobl0g.gameservice.kbo.dto.GameInfoDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KboCrawlerService {

    @Value("${external.urls.kbo.schedule}")
    private String kboScheduleUrl;

    public List<GameInfoDto> crawlSchedule(int season, int month, SeriesType seriesType) {
        log.info("크롤링 시작 - 시즌: {}, 월: {}, 시리즈: {}", season, month, seriesType.getDescription());

        try (Playwright playwright = Playwright.create()) {
            try (Browser browser = playwright.chromium().launch()) {
                try (Page page = browser.newPage()) {
                    // KBO 경기 일정 페이지 접속
                    page.navigate(kboScheduleUrl);

                    // 연도 선택
                    page.locator("#ddlYear").selectOption(String.valueOf(season));

                    // 월 선택 (01, 02, ... 형식)
                    String monthStr = String.format("%02d", month);
                    page.locator("#ddlMonth").selectOption(monthStr);

                    // 시리즈 타입 선택
                    page.locator("#ddlSeries").selectOption(seriesType.getValue());

                    // 경기 일정 테이블 로드 대기
                    page.waitForSelector("#tblScheduleList > tbody");

                    // 테이블의 모든 행 가져오기
                    List<Locator> scheduleTableRows = page.locator("#tblScheduleList > tbody > tr").all();

                    // 경기 정보 파싱
                    List<GameInfoDto> gameInfoDtoList = parseGameSchedule(scheduleTableRows, season, seriesType);

                    log.info("크롤링 완료 - 수집된 경기 수: {}", gameInfoDtoList.size());
                    return gameInfoDtoList;
                }
            }
        }
    }

    /**
     * 특정 시즌의 전체 일정 크롤링 (1월~12월)
     */
    public List<GameInfoDto> crawlFullSeason(int season, SeriesType seriesType) {
        List<GameInfoDto> allGameInfoDtos = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            try {
                List<GameInfoDto> monthlyGameInfoDtos = crawlSchedule(season, month, seriesType);
                allGameInfoDtos.addAll(monthlyGameInfoDtos);

                // 크롤링 간격 (서버 부하 방지)
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("크롤링 실패 - 시즌: {}, 월: {}", season, month, e);
            }
        }

        log.info("전체 크롤링 완료 - 총 경기 수: {}", allGameInfoDtos.size());
        return allGameInfoDtos;
    }

    /**
     * 경기 일정 HTML 파싱
     */
    private List<GameInfoDto> parseGameSchedule(List<Locator> locators, int season, SeriesType seriesType) {

        Map<String, Integer> gameCountMap = new HashMap<>();
        List<GameInfoDto> gameInfoDtoList = new ArrayList<>();
        LocalDate currentDate = LocalDate.MIN;

        for (Locator row : locators) {
            // 경기 정보가 없으면 스킵 (데이터 없음 문구 or 이동일)
            int playCellCount = row.locator("td.play").count();
            if (playCellCount == 0) {
                continue;
            }

            // 날짜 정보가 있으면 currentDate 갱신
            int dayCellCount = row.locator("td.day").count();
            if (dayCellCount > 0) {
                String dayText = row.locator("td.day").innerText().substring(0, 5).trim();
                String[] parts = dayText.split("\\.");
                int monthValue = Integer.parseInt(parts[0]);
                int dayValue = Integer.parseInt(parts[1]);
                currentDate = LocalDate.of(season, monthValue, dayValue);
            }

            // 경기 시간
            String timeText = row.locator("td.time").innerText().trim();
            LocalTime time = null;
            try {
                time = LocalTime.parse(timeText);
            } catch (Exception e) {
                log.warn("시간 파싱 실패: {}", timeText);
            }

            // 팀 정보
            Locator playCell = row.locator("td.play");
            List<String> teams = playCell.locator("> span").allInnerTexts();
            Team awayTeam = Team.findByTeamName(teams.get(0));
            Team homeTeam = Team.findByTeamName(teams.get(1));

            // gameKey 생성 및 일련번호 부여
            String gameKeyPrefix = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + awayTeam + "-" + homeTeam;
            int count = gameCountMap.getOrDefault(gameKeyPrefix, 0) + 1;
            gameCountMap.put(gameKeyPrefix, count);
            String gameKey = gameKeyPrefix + "-" + count;

            // 점수 정보
            List<String> scoreTexts = playCell.locator("em > span").allInnerTexts();
            Integer awayScore = null;
            Integer homeScore = null;
            if (scoreTexts.size() >= 2) {
                try {
                    awayScore = Integer.parseInt(scoreTexts.get(0));
                    homeScore = Integer.parseInt(scoreTexts.get(2));
                } catch (NumberFormatException e) {
                    log.warn("점수 파싱 실패: {}", scoreTexts);
                }
            }

            // 클래스 없는 나머지 컬럼들 (중계, 구장, 비고)
            List<Locator> remainCells = row.locator("td:not([class])").all();

            // 중계 정보
            String relay = remainCells.get(1).innerHTML().replace("<br>", ",").trim();

            // 구장
            String stadium = remainCells.get(3).innerText().trim();

            // 비고 (취소 사유)
            String remarkText = remainCells.get(remainCells.size() - 1).innerText().trim();
            CancellationReason cancellationReason = CancellationReason.fromString(remarkText);

            // 경기 상태 결정
            GameStatus gameStatus;
            if (cancellationReason != null) {
                gameStatus = GameStatus.CANCELLED;
            } else if (awayScore != null && homeScore != null) {
                gameStatus = GameStatus.FINISHED;
            } else {
                gameStatus = GameStatus.SCHEDULED;
            }

            // GameInfo 객체 생성
            GameInfoDto gameInfoDto = GameInfoDto.of(
                    gameKey,
                    seriesType,
                    currentDate,
                    time,
                    awayTeam,
                    homeTeam,
                    awayScore,
                    homeScore,
                    relay,
                    stadium,
                    gameStatus,
                    cancellationReason
            );

            gameInfoDtoList.add(gameInfoDto);
        }

        return gameInfoDtoList;
    }
}
