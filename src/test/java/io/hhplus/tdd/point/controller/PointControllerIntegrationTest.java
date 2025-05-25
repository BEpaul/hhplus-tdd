package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.IPointService;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
public class PointControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPointService pointService;

    /**
     * 올바른 요청
     * - (사용자의 ID) > 0
     * - (사용자의 포인트 충전 금액) > 0
     */
    @DisplayName("올바른 요청 시 사용자의 포인트를 정상적으로 충전하고 HTTP 상태코드 200과 충전된 포인트를 반환한다.")
    @Test
    void chargeUserPoint_Success() throws Exception {
        // given
        long userId = 1L;
        long chargeAmount = 1000L;
        when(pointService.chargeUserPoint(userId, chargeAmount))
                .thenReturn(new UserPoint(userId, chargeAmount, System.currentTimeMillis()));

        // when
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType("application/json")
                        .content(String.valueOf(chargeAmount)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(chargeAmount));
    }

    /**
     * 잘못된 요청
     * - (사용자의 ID) <= 0
     * OR
     * - (사용자의 포인트 충전 금액) <= 0
     */
    @DisplayName("잘못된 요청 시 사용자의 포인트를 충전하지 못하고 HTTP 상태코드 400을 반환한다.")
    @Test
    void chargeUserPoint_Failure() throws Exception {
        // given
        long userId = -1L;
        long chargeAmount = -1000L;

        // when
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType("application/json")
                        .content(String.valueOf(chargeAmount)))
                // then
                .andExpect(status().isBadRequest());
    }

    /**
     * 올바른 요청
     * - (사용자의 ID) > 0
     * - (사용자의 포인트 사용 금액) > 0
     */
    @DisplayName("올바른 요청 시 사용자의 포인트를 정상적으로 사용하고 HTTP 상태코드 200과 사용된 포인트를 반환한다.")
    @Test
    void useUserPoint_Success() throws Exception {
        // given
        long userId = 1L;
        long useAmount = 500L;
        long currentPoint = 1000L;
        long expectedPoint = currentPoint - useAmount;

        when(pointService.useUserPoint(userId, useAmount))
                .thenReturn(new UserPoint(userId, expectedPoint, System.currentTimeMillis()));

        // when
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType("application/json")
                        .content(String.valueOf(useAmount)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(expectedPoint));
    }

    /**
     * 잘못된 요청
     * - (사용자의 ID) <= 0
     * OR
     * - (사용자의 포인트 사용 금액) <= 0
     */
    @DisplayName("잘못된 요청 시 사용자의 포인트를 사용하지 못하고 HTTP 상태코드 400을 반환한다.")
    @Test
    void useUserPoint_Failure() throws Exception {
        // given
        long userId = -1L;
        long useAmount = -500L;

        // when
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType("application/json")
                        .content(String.valueOf(useAmount)))
                // then
                .andExpect(status().isBadRequest());
    }
}
