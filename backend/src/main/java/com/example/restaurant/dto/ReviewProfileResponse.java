package com.example.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewProfileResponse {

    @Schema(description = "사용자 이름", example = "송자올")
    private String name;

    @Schema(description = "경험치", example = "230")
    private int xp;

    @Schema(description = "신뢰 점수", example = "80")
    private int trustScore;

    @Schema(description = "총 작성 리뷰 수", example = "15")
    private long reviewCount;

    @Schema(description = "총 받은 좋아요 수", example = "42")
    private long likeCount;

    @Schema(description = "유저 등급명", example = "Silver")
    private String rankName;

    @Schema(description = "등급 설명", example = "활동이 활발한 사용자입니다.")
    private String rankDescription;

    @Schema(description = "등급 아이콘 URL", example = "https://example.com/silver.png")
    private String rankIcon;
}
