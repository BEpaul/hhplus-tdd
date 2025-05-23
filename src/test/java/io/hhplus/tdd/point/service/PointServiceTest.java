package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static io.hhplus.tdd.point.entity.TransactionType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        when(userPointTable.selectById(userId)).thenReturn(expectedUserPoint);

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
        PointHistory chargeHistory = new PointHistory(1L, userId, 1000, CHARGE, System.currentTimeMillis());
        PointHistory useHistory = new PointHistory(2L, userId, 500, USE, System.currentTimeMillis());

        List<PointHistory> expectedHistories = List.of(chargeHistory, useHistory);
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(expectedHistories);

        // when
        List<PointHistory> actualHistories = pointService.getPointHistoryByUserId(userId);

        // then
        assertEquals(expectedHistories, actualHistories);
    }

    @DisplayName("특정 사용자의 포인트를 충전한다.")
    @Test
    void chargePoint() {
        // given
        long userId = 1L;
        long currentAmount = 5000L;
        long chargeAmount = 1000L;
        long currentTime = System.currentTimeMillis();

        UserPoint initialUserPoint = new UserPoint(userId, currentAmount, currentTime);
        UserPoint expectedUserPoint = initialUserPoint.charge(chargeAmount);
        PointHistory pointHistory = new PointHistory(1L, userId, chargeAmount, CHARGE, currentTime);

        when(userPointTable.selectById(userId)).thenReturn(initialUserPoint);
        when(userPointTable.insertOrUpdate(eq(userId), eq(expectedUserPoint.point()))).thenReturn(expectedUserPoint);
        when(pointHistoryTable.insert(eq(userId), eq(chargeAmount), eq(CHARGE), anyLong())).thenReturn(pointHistory);

        // when
        UserPoint actualUserPoint = pointService.chargeUserPoint(userId, chargeAmount);

        // then
        assertEquals(expectedUserPoint.point(), actualUserPoint.point());
        assertEquals(expectedUserPoint.id(), actualUserPoint.id());
    }

    @DisplayName("특정 사용자의 포인트를 사용한다.")
    @Test
    void usePoint() {
        // given
        long userId = 1L;
        long currentAmount = 5000L;
        long useAmount = 1000L;
        long currentTime = System.currentTimeMillis();

        UserPoint initialUserPoint = new UserPoint(userId, currentAmount, currentTime);
        UserPoint expectedUserPoint = initialUserPoint.use(useAmount);
        PointHistory pointHistory = new PointHistory(1L, userId, useAmount, USE, currentTime);

        when(userPointTable.selectById(userId)).thenReturn(initialUserPoint);
        when(userPointTable.insertOrUpdate(eq(userId), eq(expectedUserPoint.point()))).thenReturn(expectedUserPoint);
        when(pointHistoryTable.insert(eq(userId), eq(useAmount), eq(USE), anyLong())).thenReturn(pointHistory);

        // when
        UserPoint actualUserPoint = pointService.useUserPoint(userId, useAmount);

        // then
        assertEquals(expectedUserPoint.point(), actualUserPoint.point());
        assertEquals(expectedUserPoint.id(), actualUserPoint.id());
    }
}
