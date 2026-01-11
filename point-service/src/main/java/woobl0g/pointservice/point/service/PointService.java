package woobl0g.pointservice.point.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserClient userClient;

    @Transactional
    public void addPoints(AddPointRequestDto dto) {
        Point point = pointRepository.findByUserId(dto.getUserId())
                .orElseGet(() -> pointRepository.save(
                        Point.create(dto.getUserId(), 0)
                ));

        int pointChange = dto.getActionType().getAmount();
        String reason = dto.getActionType().name();

        point.addAmount(pointChange);

        // 포인트 이력 저장
        pointHistoryRepository.save(PointHistory.create(
                dto.getUserId(),
                pointChange,
                reason
        ));
    }

    @Transactional
    public void deductPoints(DeductPointRequestDto dto) {
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
    }

    @Transactional(readOnly = true)
    public PageResponse<PointHistoryResponseDto> getPointHistory(Long userId, Pageable pageable) {
        if (!pointRepository.existsByUserId(userId)) {
            throw new PointException(ResponseCode.POINT_NOT_FOUND);
        }

        Page<PointHistory> histories = pointHistoryRepository.findByUserId(userId, pageable);

        return PageResponse.of(histories.map(PointHistoryResponseDto::from));
    }

    @Transactional(readOnly = true)
    public List<PointRankingResponseDto> getPointRanking(Pageable pageable) {
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
