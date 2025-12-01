package com.example.restaurant.service;

import com.example.restaurant.dto.ReviewProfileResponse;
import com.example.restaurant.entity.User;
import com.example.restaurant.entity.UserRank;
import com.example.restaurant.repository.ReviewLikeRepository;
import com.example.restaurant.repository.ReviewRepository;
import com.example.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewProfileService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository likeRepository;

    private int calculateTrustScore(long reviewCount, long likeCount) {
        int score = (int)(reviewCount * 5 + likeCount * 2);
        return Math.min(score, 100);
    }

    public ReviewProfileResponse getReviewProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + userId));

        long reviewCount = reviewRepository.countByUser_Id(userId);
        long likeCount = likeRepository.countLikesByUserId(userId);
        int trustScore = calculateTrustScore(reviewCount, likeCount);

        // ⭐ 랭크 안전 처리
        UserRank rank = user.getRank();

        String rankName = (rank != null) ? rank.getRankName() : "Unranked";
        String rankDescription = (rank != null) ? rank.getDescription() : "활동을 시작해보세요!";
        String rankIcon = (rank != null) ? rank.getIconUrl() : null;

        return new ReviewProfileResponse(
                user.getName(),
                user.getXp(),
                trustScore,
                reviewCount,
                likeCount,
                rankName,
                rankDescription,
                rankIcon
        );
    }
}
