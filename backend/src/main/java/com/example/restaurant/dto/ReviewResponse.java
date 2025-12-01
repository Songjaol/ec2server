package com.example.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewResponse {

    @Schema(description = "리뷰 ID", example = "100")
    private Long id;

    @Schema(description = "작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "식당 ID", example = "10")
    private Long restaurantId;

    @Schema(description = "식당 이름", example = "곱창고 수원점")
    private String restaurantName;

    @Schema(description = "식당 이미지 URL", example = "https://example.com/food.jpg")
    private String restaurantImageUrl;

    @Schema(description = "평점", example = "4")
    private Integer rating;

    @Schema(description = "리뷰 내용", example = "재방문 의사 200%입니다!")
    private String content;

    @Schema(description = "방문 날짜(문자열)", example = "2025-02-01")
    private String visitedDate;

    @Schema(description = "좋아요 수", example = "35")
    private Integer likes;

    @Schema(description = "작성 날짜", example = "2025-02-01T12:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "작성자 등급", example = "Bronze")
    private String rankName;

    @Schema(description = "등급 설명", example = "첫 리뷰를 작성했습니다!")
    private String rankDescription;

    @Schema(description = "등급 아이콘 URL", example = "https://example.com/bronze.png")
    private String rankIcon;
}
