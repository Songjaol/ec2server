package com.example.restaurant.controller;

import com.example.restaurant.dto.FoodRecommendationDto;
import com.example.restaurant.entity.Restaurant;
import com.example.restaurant.service.AIRecommendService;
import com.example.restaurant.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
@Tag(name = "추천 API", description = "음식 및 맛집 추천 API")
public class RecommendController {

    private final RecommendService recommendService;
    private final AIRecommendService aiRecommendService;

    @Operation(summary = "음식 추천", description = "기분(mood)과 음식 타입(foodType)에 따라 음식 추천")
    @GetMapping("/foods")
    public ResponseEntity<List<FoodRecommendationDto>> recommendFoods(
            @Parameter(description = "유저 ID") @RequestParam Long userId,
            @Parameter(description = "기분") @RequestParam String mood,
            @Parameter(description = "음식 타입") @RequestParam String foodType
    ) {
        return ResponseEntity.ok(recommendService.recommendFoods(userId, mood, foodType));
    }

    @Operation(summary = "맛집 추천", description = "지역 + 음식 키워드 기반 맛집 추천 및 로그 기록")
    @GetMapping("/restaurants")
    public ResponseEntity<List<Restaurant>> getRestaurantsByFood(
            @Parameter(description = "유저 ID") @RequestParam Long userId,
            @Parameter(description = "지역") @RequestParam String region,
            @Parameter(description = "음식 키워드") @RequestParam String food
    ) {
        return ResponseEntity.ok(recommendService.getRestaurantsByRegionAndFood(userId, region, food));
    }
}
