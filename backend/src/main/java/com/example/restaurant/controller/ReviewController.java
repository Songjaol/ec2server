package com.example.restaurant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.restaurant.dto.ReviewRequest;
import com.example.restaurant.dto.ReviewResponse;
import com.example.restaurant.entity.Review;
import com.example.restaurant.entity.UserRank;
import com.example.restaurant.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰 API", description = "리뷰 작성 / 조회 API")   // ⭐ 문서화 추가
public class ReviewController {

    private final ReviewService reviewService;

    // ⭐⭐ 리뷰 저장 (엔티티 대신 DTO 반환) ⭐⭐
    @Operation(summary = "리뷰 작성", description = "ReviewRequest DTO를 받아 리뷰를 저장하고 ReviewResponse DTO로 반환합니다.")
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Parameter(description = "리뷰 요청 DTO") @RequestBody ReviewRequest request
    ) {
        Review review = reviewService.createReview(request);
        return ResponseEntity.ok(convertToResponseDto(review));
    }

    /** 리뷰 상세 조회 */
    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰 ID로 리뷰를 조회합니다.")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId
    ) {
        Review review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(convertToResponseDto(review));
    }

    // ⭐⭐ 유저의 전체 리뷰 조회 (List<DTO> 반환) ⭐⭐
    @Operation(summary = "유저 리뷰 전체 조회", description = "특정 유저가 작성한 모든 리뷰를 반환합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(
            @Parameter(description = "유저 ID") @PathVariable Long userId
    ) {
        List<Review> reviews = reviewService.getUserReviews(userId);
        List<ReviewResponse> responses = reviews.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // ⭐⭐ 유저 + 레스토랑으로 리뷰 하나 찾기 (DTO 반환) ⭐⭐
    @Operation(summary = "유저 + 식당 리뷰 조회", description = "특정 유저가 특정 식당에 작성한 리뷰를 조회합니다.")
    @GetMapping("/user/{userId}/restaurant/{restaurantId}")
    public ResponseEntity<ReviewResponse> getUserRestaurantReview(
            @Parameter(description = "유저 ID") @PathVariable Long userId,
            @Parameter(description = "식당 ID") @PathVariable Long restaurantId
    ) {
        Review review = reviewService.getReviewByUserAndRestaurant(userId, restaurantId);

        if (review == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToResponseDto(review));
    }

    /** 💡 Review 엔티티를 ReviewResponse DTO로 변환하는 헬퍼 메서드 */
    private ReviewResponse convertToResponseDto(Review review) {
        var user = review.getUser();
        var restaurant = review.getRestaurant();

        if (user == null || restaurant == null) {
            throw new RuntimeException("Review entity has null User or Restaurant reference.");
        }

        UserRank rank = user.getRank();

        return new ReviewResponse(
                review.getId(),
                user.getId(),
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getImageUrl(),
                review.getRating(),
                review.getContent(),
                review.getVisitedDate(),
                review.getLikes(),
                review.getCreatedAt(),
                rank != null ? rank.getRankName() : "Unranked",
                rank != null ? rank.getDescription() : "아직 등급이 없습니다.",
                rank != null ? rank.getIconUrl() : null
        );
    }
}
