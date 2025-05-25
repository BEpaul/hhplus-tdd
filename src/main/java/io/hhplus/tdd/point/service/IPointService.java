package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;

import java.util.List;

public interface IPointService {
    /**
     * 특정 ID를 가진 사용자의 포인트 정보를 조회합니다.
     *
     * @param id 사용자 ID
     * @return UserPoint 객체
     */
    UserPoint getUserPointById(long id);

    /**
     * 특정 사용자의 포인트 충전/이용 내역을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return PointHistory 리스트
     */
    List<PointHistory> getPointHistoryByUserId(long userId);

    /**
     * 사용자의 포인트를 충전합니다.
     *
     * @param userId 사용자 ID
     * @param amount 충전할 포인트 금액
     * @return 충전된 UserPoint 객체
     */
    UserPoint chargeUserPoint(long userId, long amount);

    /**
     * 사용자의 포인트를 사용합니다.
     *
     * @param userId 사용자 ID
     * @param amount 이용할 포인트 금액
     * @return 이용된 UserPoint 객체
     */
    UserPoint useUserPoint(long userId, long amount);
} 