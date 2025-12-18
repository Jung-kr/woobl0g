package woobl0g.userservice.health.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woobl0g.userservice.global.response.ApiResponse;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.global.exception.BaseException;

@RestController
@RequestMapping("/api/health")
public class HealthControllerImpl implements HealthController {

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<Void>> healthCheck(String value) {
        if(value != null) {
            throw new BaseException(ResponseCode.INVALID_REQUEST);
        }

        return ResponseEntity
                .status(ResponseCode.HEALTH_CHECK_SUCCESS.getStatus())
                .body(ApiResponse.success(ResponseCode.HEALTH_CHECK_SUCCESS));
    }
}
