package com.example.restaurant.service;

import com.example.restaurant.dto.FoodRecommendationDto;
import com.example.restaurant.entity.User;
import com.example.restaurant.repository.UserFoodLogRepository;
import com.example.restaurant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class RecommendServiceTest {

    @Mock
    private AIRecommendService aiRecommendService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFoodLogRepository userFoodLogRepository;

    @InjectMocks
    private RecommendService recommendService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void recommendFoods_returnsData() {

        // 1) UserRepository mock
        when(userRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(new User()));

        // 2) count() mock → 반드시 1 이상이어야 기본 추천이 안 나온다
        when(userFoodLogRepository.count())
                .thenReturn(10L);

        // 3) 최근 로그 mock
        when(userFoodLogRepository.findByUserId(anyLong()))
                .thenReturn(Collections.emptyList());

        // 4) AI 추천 mock
        when(aiRecommendService.getAIRecommendations(anyLong(), anyString(), anyString()))
                .thenReturn(List.of(
                        new FoodRecommendationDto("비빔밥", "한식")
                ));

        List<FoodRecommendationDto> list =
                recommendService.recommendFoods(1L, "행복", "한식");

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo("비빔밥");
    }

}