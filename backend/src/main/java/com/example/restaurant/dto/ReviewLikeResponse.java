package com.example.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewLikeResponse {

    @Schema(description = "좋아요 ID", example = "12")
    private Long id;

    @Schema(description = "식당 ID", example = "50")
    private Long restaurantId;

    @Schema(description = "식당 이름", example = "곱창고 수원점")
    private String restaurantName;

    @Schema(description = "식당 카테고리", example = "한식")
    private String restaurantCategory;

    @Schema(description = "식당 주소", example = "경기도 수원시 팔달구 매산로 12")
    private String restaurantAddress;
}
