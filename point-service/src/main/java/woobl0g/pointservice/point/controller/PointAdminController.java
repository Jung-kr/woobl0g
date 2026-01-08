package woobl0g.pointservice.point.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woobl0g.pointservice.global.response.ApiResponse;
import woobl0g.pointservice.global.response.ResponseCode;
import woobl0g.pointservice.point.dto.AddPointRequestDto;
import woobl0g.pointservice.point.dto.DeductPointRequestDto;
import woobl0g.pointservice.point.service.PointService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/points")
public class PointAdminController {

    private final PointService pointService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addPointByAdmin(@RequestBody AddPointRequestDto addPointRequestDto) {
        pointService.addPoints(addPointRequestDto);
        return ResponseEntity
                .status(ResponseCode.POINT_ADD_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.POINT_ADD_SUCCESS));
    }

    @PostMapping("/deduct")
    public ResponseEntity<ApiResponse<Void>> deductPointByAdmin(@RequestBody DeductPointRequestDto DeductPointRequestDto) {
        pointService.deductPoints(DeductPointRequestDto);
        return ResponseEntity
                .status(ResponseCode.POINT_ADD_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.POINT_ADD_SUCCESS));
    }
}
