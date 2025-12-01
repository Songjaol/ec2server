package com.example.restaurant.service;

import com.example.restaurant.dto.FoodRecommendationDto;
import com.example.restaurant.entity.User;
import com.example.restaurant.entity.UserFoodLog;
import com.example.restaurant.repository.RestaurantRepository;
import com.example.restaurant.repository.UserFoodLogRepository;
import com.example.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;   // ✅ 요거! 스프링 캐시
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIRecommendService {

    private final UserFoodLogRepository userFoodLogRepository;
    private final RestaurantRepository restaurantRepository;
    private final KakaoApiService kakaoApiService;
    private final UserRepository userRepository;

    /**
     * AI 추천 (카테고리 기반)
     */
    @Cacheable(value = "recommendations", key = "#userId + '-' + #mood + '-' + #foodType")
    public List<FoodRecommendationDto> getAIRecommendations(Long userId,
                                                            String mood,
                                                            String foodType) {

        // 0) 유저
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1) 해당 mood 로그 전부 가져오기
        List<UserFoodLog> moodLogs = userFoodLogRepository.findByMood(mood);
        if (moodLogs.isEmpty()) {
            return fallbackCategory(foodType);
        }

        // 2) 같은 category만 필터
        List<UserFoodLog> categoryLogs = moodLogs.stream()
                .filter(l -> foodType.equals(l.getFoodType()))
                .toList();

        if (categoryLogs.isEmpty()) {
            return fallbackCategory(foodType);
        }

        // 3) 인기 음식명 top 30 (시간 가중치)
        Map<String, Double> scoreMap = new HashMap<>();
        for (UserFoodLog log : categoryLogs) {

            long daysAgo = ChronoUnit.DAYS.between(log.getCreatedAt(), LocalDateTime.now());
            double timeWeight = Math.exp(-0.05 * daysAgo);

            scoreMap.merge(log.getFoodName(), timeWeight, Double::sum);
        }

        List<String> topFoods = scoreMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(30)
                .map(Map.Entry::getKey)
                .toList();

        if (topFoods.isEmpty()) {
            return fallbackCategory(foodType);
        }

        // 4) 메뉴명 + 이미지 반환
        List<FoodRecommendationDto> result = new ArrayList<>();
        for (String foodName : topFoods) {
            result.add(new FoodRecommendationDto(
                    foodName,
                    foodType
            ));
        }

        return result;
    }

    /**
     * 카테고리 fallback (카테고리 기반 인기 음식)
     */
    private List<FoodRecommendationDto> fallbackCategory(String category) {
        List<Object[]> rows = userFoodLogRepository.findPopularFoodsByFoodType(category);

        return rows.stream()
                .limit(20)
                .map(r -> {
                    String foodName = (String) r[0];
                    return new FoodRecommendationDto(
                            foodName,
                            category
                    );
                })
                .toList();
    }
}
