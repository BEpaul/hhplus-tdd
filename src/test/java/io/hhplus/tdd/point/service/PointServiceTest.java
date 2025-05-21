package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.dto.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @Mock
    private UserPointTable userPointTable;

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
}
