package woobl0g.pointservice.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woobl0g.pointservice.global.response.ResponseCode;
import woobl0g.pointservice.point.dto.AddPointRequestDto;
import woobl0g.pointservice.point.dto.DeductPointRequestDto;
import woobl0g.pointservice.point.service.PointService;

@RestController
@RequestMapping("/internal/points")
@RequiredArgsConstructor
public class PointInternalController {

    private final PointService pointService;

    @PostMapping("/add")
    public ResponseEntity<Void> addPoints(@RequestBody AddPointRequestDto addPointRequestDto) {
        pointService.addPoints(addPointRequestDto);
        return ResponseEntity
                .status(ResponseCode.POINT_ADD_SUCCESS.getStatus())
                .build();
    }

    @PostMapping("/deduct")
    public ResponseEntity<Void> deductPoints(@RequestBody DeductPointRequestDto deductPointRequestDto) {
        pointService.deductPoints(deductPointRequestDto);
        return ResponseEntity
                .status(ResponseCode.POINT_DEDUCT_SUCCESS.getStatus())
                .build();
    }
}
