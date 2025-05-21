package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @Test
    void getPoint() throws Exception {
        // given
        long userId = 1L;
        UserPoint expectedUserPoint = new UserPoint(userId, 1000, System.currentTimeMillis());
        when(pointService.getUserPointById(userId)).thenReturn(expectedUserPoint);

        // when
        mockMvc.perform(get("/point/{id}", userId))
                // then
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"point\":100,\"updateMillis\":" + expectedUserPoint.updateMillis() + "}"));
    }
}
