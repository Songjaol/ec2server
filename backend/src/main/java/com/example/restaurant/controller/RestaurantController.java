package com.example.restaurant.controller;

import com.example.restaurant.entity.Restaurant;
import com.example.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;   // ← 문서화 추가
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
@Tag(name = "식당 API", description = "식당 조회 관련 API")   // ← 문서화 추가
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(summary = "전체 식당 목록 조회", description = "DB에 저장된 모든 식당 정보를 반환합니다.")   // ← 문서화 추가
    @GetMapping
    public List<Restaurant> getRestaurants() {
        return restaurantService.getAllRestaurants();
    }
}
