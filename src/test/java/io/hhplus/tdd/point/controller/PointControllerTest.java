package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.IPointService;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static io.hhplus.tdd.point.entity.TransactionType.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPointService pointService;

    @DisplayName("특정 ID를 가진 사용자의 포인트를 조회한다.")
    @Test
    void point() throws Exception {
        // given
        long userId = 1L;
        UserPoint expectedUserPoint = new UserPoint(userId, 1000, System.currentTimeMillis());
        when(pointService.getUserPointById(userId)).thenReturn(expectedUserPoint);

        // when
        mockMvc.perform(get("/point/{id}", userId))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUserPoint.id()))
                .andExpect(jsonPath("$.point").value(expectedUserPoint.point()))
                .andExpect(jsonPath("$.updateMillis").value(expectedUserPoint.updateMillis()))
                .andDo(print());
    }

    @DisplayName("특정 ID를 가진 사용자의 포인트 충전/이용 내역을 조회한다.")
    @Test
    void history() throws Exception {
        // given
        long userId = 1L;
        PointHistory chargeHistory = new PointHistory(1L, userId, 1000, CHARGE, System.currentTimeMillis());
        PointHistory useHistory = new PointHistory(2L, userId, 1000, USE, System.currentTimeMillis());

        List<PointHistory> expectedHistories = List.of(chargeHistory, useHistory);
        when(pointService.getPointHistoryByUserId(userId)).thenReturn(expectedHistories);

        // when
        mockMvc.perform(get("/point/{id}/histories", userId))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(chargeHistory.id()))
                .andExpect(jsonPath("$[0].userId").value(chargeHistory.userId()))
                .andExpect(jsonPath("$[0].amount").value(chargeHistory.amount()))
                .andExpect(jsonPath("$[0].type").value(chargeHistory.type().toString()))
                .andExpect(jsonPath("$[0].updateMillis").value(chargeHistory.updateMillis()))
                .andExpect(jsonPath("$[1].id").value(useHistory.id()))
                .andExpect(jsonPath("$[1].userId").value(useHistory.userId()))
                .andExpect(jsonPath("$[1].amount").value(useHistory.amount()))
                .andExpect(jsonPath("$[1].type").value(useHistory.type().toString()))
                .andExpect(jsonPath("$[1].updateMillis").value(useHistory.updateMillis()))
                .andDo(print());
    }
}
