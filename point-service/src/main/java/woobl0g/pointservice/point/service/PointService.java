package woobl0g.pointservice.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.pointservice.global.exception.PointException;
import woobl0g.pointservice.global.response.ResponseCode;
import woobl0g.pointservice.point.client.UserClient;
import woobl0g.pointservice.point.domain.Point;
import woobl0g.pointservice.point.domain.PointHistory;
import woobl0g.pointservice.point.dto.*;
import woobl0g.pointservice.point.repository.PointHistoryRepository;
import woobl0g.pointservice.point.repository.PointRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserClient userClient;

    @Transactional
    public void addPoints(AddPointRequestDto dto) {
        log.debug("포인트 적립 시작: userId={}, actionType={}", dto.getUserId(), dto.getActionType());
        
        Point point = pointRepository.findByUserId(dto.getUserId())
                .orElseGet(() -> {
                    log.info("신규 포인트 엔티티 생성: userId={}", dto.getUserId());
                    return pointRepository.save(Point.create(dto.getUserId(), 0));
                });

        int pointChange = dto.getActionType().getAmount();
        String reason = dto.getActionType().name();

        point.addAmount(pointChange);

        // 포인트 이력 저장
        pointHistoryRepository.save(PointHistory.create(
                dto.getUserId(),
                pointChange,
                reason
        ));
        
        log.info("포인트 적립 완료: userId={}, amount={}, reason={}", dto.getUserId(), pointChange, reason);
    }

    @Transactional
    public void deductPoints(DeductPointRequestDto dto) {
        log.info("포인트 차감 시작: userId={}, actionType={}", dto.getUserId(), dto.getActionType());
        
        Point point = pointRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new PointException(ResponseCode.POINT_NOT_FOUND));

        int pointChange = -dto.getActionType().getAmount();
        String reason = dto.getActionType().name();

        point.deductAmount(dto.getActionType().getAmount());

        // 포인트 이력 저장
        pointHistoryRepository.save(PointHistory.create(
                dto.getUserId(),
                pointChange,
                reason
        ));
        
        log.info("포인트 차감 완료: userId={}, amount={}, reason={}", dto.getUserId(), pointChange, reason);
    }

    @Transactional(readOnly = true)
    public PageResponse<PointHistoryResponseDto> getPointHistory(Long userId, Pageable pageable) {
        log.debug("포인트 이력 조회: userId={}, page={}", userId, pageable.getPageNumber());
        
        if (!pointRepository.existsByUserId(userId)) {
            log.warn("포인트 이력 조회 실패 - 포인트 없음: userId={}", userId);
            throw new PointException(ResponseCode.POINT_NOT_FOUND);
        }

        Page<PointHistory> histories = pointHistoryRepository.findByUserId(userId, pageable);

        return PageResponse.of(histories.map(PointHistoryResponseDto::from));
    }

    @Transactional(readOnly = true)
    public List<PointRankingResponseDto> getPointRanking(Pageable pageable) {
        log.debug("포인트 랭킹 조회: page={}", pageable.getPageNumber());
        
        List<Point> rankings = pointRepository.findAll(pageable).getContent();

        List<Long> userIds = rankings.stream()
                .map(Point::getUserId)
                .toList();

        List<UserResponseDto> userResponseDtos = userClient.fetchUsers(userIds);
        Map<Long, UserInfoDto> userInfoMap = new HashMap<>();
        for (UserResponseDto userResponseDto : userResponseDtos) {
            Long userId = userResponseDto.getUserId();
            String email = userResponseDto.getEmail();
            String name = userResponseDto.getName();
            userInfoMap.put(userId, UserInfoDto.of(email, name));
        }

        return IntStream.range(0, rankings.size())
                .mapToObj(i -> PointRankingResponseDto.of(i + 1, rankings.get(i).getAmount(), userInfoMap.get(userIds.get(i))))
                .toList();
    }
}
