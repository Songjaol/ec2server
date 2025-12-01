package com.example.restaurant.service;

import com.example.restaurant.dto.ReviewRequest;
import com.example.restaurant.entity.Restaurant;
import com.example.restaurant.entity.Review;
import com.example.restaurant.entity.User;
import com.example.restaurant.entity.UserRank;
import com.example.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리를 위해 추가

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewLikeRepository likeRepository;
    private final UserRankRepository userRankRepository;

    /** ⭐ 리뷰 생성 및 유저 정보 업데이트 */
    @Transactional // 하나의 트랜잭션으로 처리하여 데이터 일관성 보장
    public Review createReview(ReviewRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // 1. 리뷰 엔티티 생성 및 저장
        Review review = new Review();
        review.setUser(user);
        review.setRestaurant(restaurant);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setVisitedDate(request.getVisitedDate());

        Review saved = reviewRepository.save(review);

        // 2. 유저 XP 업데이트 (XP + 10)
        int newXp = user.getXp() + 10;
        user.setXp(newXp);

        // 3. 랭크 자동 업데이트 (NullPointerException 방지 로직 추가)
        updateUserRank(user, newXp);

        // 4. 신뢰도 갱신
        long reviewCount = reviewRepository.countByUser_Id(user.getId());
        long likeCount = likeRepository.countLikesByUserId(user.getId());
        user.setTrustScore(calculateTrustScore(reviewCount, likeCount));

        // 5. 유저 정보 최종 저장 (User 엔티티의 변경 사항 반영)
        userRepository.save(user);

        return saved;
    }

    /** ⭐ 리뷰 상세 조회 추가 */
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    /** * ⭐ XP 기반 랭크 자동 업데이트
     * - 현재 XP와 정확히 일치하는 랭크를 찾습니다.
     * - 만약 DB에 해당 XP를 가진 랭크가 없다면 NullPointerException을 방지합니다.
     * * @param user 업데이트할 User 엔티티
     * @param xp 사용자의 새 XP
     */
    private void updateUserRank(User user, int xp) {
        // 기존 코드: userRankRepository.findByXp(xp)는 정확히 XP가 일치하는 랭크만 찾습니다.
        // 이상적인 구현은 'userRankRepository.findTopByXpLessThanEqualOrderByXpDesc(xp)'를 사용하여
        // 현재 XP보다 작거나 같은 랭크 중 가장 높은 랭크를 찾는 것입니다.
        // 여기서는 기존 findByXp를 유지하되, Null-Safe하게 만듭니다.

        UserRank rank = userRankRepository.findByXp(xp);

        // 🚨 중요: rank가 null인지 확인하여 NullPointerException 방지
        if (rank != null) {
            user.setRank(rank);
        }
        // 만약 rank가 null이라면 (XP가 랭크 조건에 정확히 맞지 않다면),
        // user의 기존 rank를 유지하거나, 기본 랭크로 설정하는 추가 로직이 필요할 수 있습니다.
        // 여기서는 기존 랭크를 유지하도록 처리합니다.
    }

    /** ⭐ 신뢰도 계산 */
    private int calculateTrustScore(long reviewCount, long likeCount) {
        if (reviewCount == 0) return 0;
        // (받은 좋아요 수 / 작성한 리뷰 수) * 100.0
        double score = (double) likeCount / reviewCount * 100.0;
        return (int) Math.min(score, 100); // 최대 100을 넘지 않도록 제한
    }

    /** ⭐ 특정 유저의 리뷰 목록 조회 */
    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUser_Id(userId);
    }

    /** ⭐ 특정 유저가 특정 식당에 작성한 리뷰 조회 */
    public Review getReviewByUserAndRestaurant(Long userId, Long restaurantId) {
        return reviewRepository.findByUser_IdAndRestaurant_Id(userId, restaurantId)
                .orElse(null); // 리뷰가 없을 경우 null 반환
    }

}