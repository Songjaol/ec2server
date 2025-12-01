package com.example.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    @Schema(description = "유저 ID", example = "1")
    private Long id;

    @Schema(description = "이름", example = "송자올")
    private String name;

    @Schema(description = "이메일", example = "test@example.com")
    private String email;

    @Schema(description = "현재 기분", example = "행복")
    private String mood;

    @Schema(description = "선호 음식 타입", example = "한식")
    private String foodType;

    @Schema(description = "선호 지역", example = "수원")
    private String region;

    @Schema(description = "경험치", example = "240")
    private int xp;

    @Schema(description = "등급명", example = "Silver")
    private String rankName;

    @Schema(description = "등급 설명", example = "활동이 활발한 사용자입니다.")
    private String rankDescription;

    @Schema(description = "등급 아이콘 URL", example = "https://example.com/silver.png")
    private String rankIcon;
}
