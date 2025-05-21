package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.TransactionType;
import io.hhplus.tdd.point.dto.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointService pointService;


    @DisplayName("특정 ID를 가진 사용자의 포인트를 조회한다.")
    @Test
    void getPointByUserId() {
        // given
        long userId = 1L;
        UserPoint expectedUserPoint = new UserPoint(userId, 1000, System.currentTimeMillis());
        when(userPointTable.selectById(1L)).thenReturn(expectedUserPoint);

        // when
        UserPoint actualUserPoint = pointService.getUserPointById(userId);

        // then
        assertEquals(expectedUserPoint, actualUserPoint);
    }

    @DisplayName("특정 사용자의 포인트 충전/이용 내역을 조회한다.")
    @Test
    void getPointHistoryByUserId() {
        // given
        long userId = 1L;
        PointHistory chargeHistory = new PointHistory(1L, userId, 1000, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory useHistory = new PointHistory(2L, userId, 500, TransactionType.USE, System.currentTimeMillis());

        List<PointHistory> expectedHistories = List.of(chargeHistory, useHistory);
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(expectedHistories);

        // when
        List<PointHistory> actualHistories = pointService.getPointHistoryByUserId(userId);

        // then
        assertEquals(expectedHistories, actualHistories);
    }
}
