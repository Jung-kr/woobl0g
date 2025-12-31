package woobl0g.pointservice.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.pointservice.global.exception.PointException;
import woobl0g.pointservice.global.response.ResponseCode;
import woobl0g.pointservice.point.domain.Point;
import woobl0g.pointservice.point.dto.AddPointRequestDto;
import woobl0g.pointservice.point.dto.DeductPointRequestDto;
import woobl0g.pointservice.point.repository.PointRepository;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public void addPoints(AddPointRequestDto dto) {
        Point point = pointRepository.findByUserId(dto.getUserId())
                .orElseGet(() -> pointRepository.save(
                        Point.create(dto.getUserId(), 0)
                ));

        point.addAmount(dto.getActionType().getAmount());
    }

    @Transactional
    public void deductPoints(DeductPointRequestDto deductPointRequestDto) {
        Point point = pointRepository.findByUserId(deductPointRequestDto.getUserId())
                .orElseThrow(() -> new PointException(ResponseCode.POINT_NOT_FOUND));

        point.deductAmount(deductPointRequestDto.getActionType().getAmount());
    }
}
