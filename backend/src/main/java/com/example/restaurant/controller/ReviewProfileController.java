package com.example.restaurant.controller;

import com.example.restaurant.dto.ReviewProfileResponse;
import com.example.restaurant.service.ReviewProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review-profile")
@Tag(name = "리뷰 프로필 API", description = "유저의 리뷰 통계/프로필 조회 API")   // ⭐ 문서화 추가
public class ReviewProfileController {

    private final ReviewProfileService reviewProfileService;

    @Operation(
            summary = "유저 리뷰 프로필 조회",
            description = "유저의 리뷰 수, 평균 평점, 좋아요 수 등의 리뷰 통계를 조회합니다."
    )
    @GetMapping("/{userId}")
    public ReviewProfileResponse getReviewProfile(
            @Parameter(description = "조회할 유저 ID") @PathVariable Long userId   // ⭐ 문서화 추가
    ) {
        return reviewProfileService.getReviewProfile(userId);
    }
}
