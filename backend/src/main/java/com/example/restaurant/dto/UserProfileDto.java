package com.example.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserProfileDto {

    @Schema(description = "현재 기분", example = "행복")
    private String mood;

    @Schema(description = "선호 음식 타입", example = "한식")
    private String foodType;

    @Schema(description = "선호 지역", example = "서울")
    private String region;
}
