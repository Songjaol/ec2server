package com.example.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    @Schema(description = "리뷰 작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "리뷰 대상 식당 ID", example = "10")
    private Long restaurantId;

    @Schema(description = "평점 (1~5)", example = "5")
    private int rating;

    @Schema(description = "리뷰 내용", example = "정말 맛있고 분위기 좋아요!")
    private String content;

    @Schema(description = "방문 날짜 (yyyy-MM-dd)", example = "2025-02-01")
    private String visitedDate;
}
