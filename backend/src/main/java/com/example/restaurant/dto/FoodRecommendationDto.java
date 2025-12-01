package com.example.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.*;

@Data
@Getter
@AllArgsConstructor
public class FoodRecommendationDto {

    @Schema(description = "추천 음식 이름", example = "삼겹살")
    private String name;

    @Schema(description = "음식 종류 또는 카테고리", example = "한식")
    private String foodType;
}
