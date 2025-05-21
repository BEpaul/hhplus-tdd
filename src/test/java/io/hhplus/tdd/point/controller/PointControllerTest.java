package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

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
}
