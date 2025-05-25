package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.IPointService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PointController {

    private final IPointService pointService;

    /**
     * 특정 사용자 포인트 조회
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable @Positive(message = "사용자 ID는 양수입니다.") long id
    ) {
        log.info("Fetching points for user with ID: {}", id);
        return pointService.getUserPointById(id);
    }

    /**
     * 특정 사용자의 포인트 충전/이용 내역 조회
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable @Positive(message = "사용자 ID는 양수입니다.") long id
    ) {
        log.info("Fetching point history for user with ID: {}", id);
        return pointService.getPointHistoryByUserId(id);
    }


    /**
     * 특정 사용자의 포인트 충전
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable @Positive(message = "사용자 ID는 양수입니다.") long id,
            @RequestBody @Positive(message = "충전할 포인트는 양수입니다.") long amount
    ) {
        log.info("Charging {} points to user with ID: {}", amount, id);
        return pointService.chargeUserPoint(id, amount);
    }

    /**
     * 특정 사용자의 포인트 사용
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable @Positive(message = "사용자 ID는 양수입니다.") long id,
            @RequestBody @Positive(message = "사용할 포인트는 양수입니다.") long amount
    ) {
        log.info("Using {} points for user with ID: {}", amount, id);
        return pointService.useUserPoint(id, amount);
    }
}
