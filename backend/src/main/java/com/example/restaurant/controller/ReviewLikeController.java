package com.example.restaurant.controller;

import com.example.restaurant.dto.ReviewLikeResponse;
import com.example.restaurant.entity.ReviewLike;
import com.example.restaurant.repository.ReviewLikeRepository;
import com.example.restaurant.repository.RestaurantRepository;
import com.example.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review-likes")
@Tag(name = "리뷰 좋아요 API", description = "리뷰 좋아요/취소 및 좋아요 목록 조회 API")  // ← 문서화 추가
public class ReviewLikeController {

    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    /** ❤️ 좋아요 하기 */
    @Operation(
            summary = "리뷰 좋아요 등록",
            description = "유저 ID와 식당 ID를 받아 해당 식당에 좋아요를 추가합니다. 이미 좋아요한 경우 에러를 반환합니다."
    )
    @PostMapping
    public ResponseEntity<?> likeRestaurant(
            @Parameter(description = "userId, restaurantId, reviewText, likedIndex 포함 JSON")
            @RequestBody Map<String, Object> body
    ) {

        Long userId = Long.valueOf(body.get("userId").toString());
        Long restaurantId = Long.valueOf(body.get("restaurantId").toString());
        String reviewText = body.containsKey("reviewText") ? body.get("reviewText").toString() : null;
        int likedIndex = body.containsKey("likedIndex") ? Integer.parseInt(body.get("likedIndex").toString()) : -1;

        if (reviewLikeRepository.existsByUser_IdAndRestaurant_Id(userId, restaurantId)) {
            return ResponseEntity.badRequest().body("이미 좋아요한 식당입니다.");
        }

        var like = new ReviewLike();
        like.setUser(userRepository.findById(userId).orElseThrow());
        like.setRestaurant(restaurantRepository.findById(restaurantId).orElseThrow());
        like.setReviewText(reviewText);
        like.setLikedIndex(likedIndex);

        reviewLikeRepository.save(like);
        return ResponseEntity.ok("좋아요 저장 완료!");
    }

    /** 💔 좋아요 취소 */
    @Operation(
            summary = "리뷰 좋아요 취소",
            description = "유저 ID와 식당 ID를 받아 해당 좋아요를 삭제합니다."
    )
    @DeleteMapping
    public ResponseEntity<?> unlike(
            @Parameter(description = "userId, restaurantId 포함 JSON")
            @RequestBody Map<String, Long> body
    ) {
        Long userId = body.get("userId");
        Long restaurantId = body.get("restaurantId");
        reviewLikeRepository.deleteByUser_IdAndRestaurant_Id(userId, restaurantId);
        return ResponseEntity.ok("좋아요 취소 완료!");
    }

    /** ⭐ 좋아요한 식당 목록 DTO로 반환 */
    @Operation(
            summary = "좋아요한 식당 목록 조회",
            description = "특정 유저가 좋아요한 식당 리스트를 DTO 형태로 반환합니다."
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewLikeResponse>> getLikedRestaurants(
            @Parameter(description = "유저 ID") @PathVariable Long userId
    ) {

        List<ReviewLikeResponse> dtos = reviewLikeRepository.findByUser_Id(userId)
                .stream()
                .map(l -> new ReviewLikeResponse(
                        l.getId(),
                        l.getRestaurant().getId(),
                        l.getRestaurant().getName(),
                        l.getRestaurant().getCategory(),
                        l.getRestaurant().getAddress()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
