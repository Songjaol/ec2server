package com.example.restaurant.controller;

import com.example.restaurant.service.AIRecommendService;
import com.example.restaurant.service.RecommendService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@WebMvcTest(RecommendController.class)
public class RecommendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendService recommendService;

    @MockBean
    private AIRecommendService aiRecommendService;

    @WithMockUser
    @Test
    void recommendFoods_success() throws Exception {

        Mockito.when(recommendService.recommendFoods(anyLong(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recommend/foods")
                        .param("userId", "1")
                        .param("mood", "행복")
                        .param("foodType", "한식"))
                .andExpect(status().isOk());
    }
}
