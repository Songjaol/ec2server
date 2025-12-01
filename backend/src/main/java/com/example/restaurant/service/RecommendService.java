package com.example.restaurant.service;

import com.example.restaurant.dto.FoodRecommendationDto;
import com.example.restaurant.entity.Restaurant;
import com.example.restaurant.entity.User;
import com.example.restaurant.entity.UserFoodLog;
import com.example.restaurant.repository.RestaurantRepository;
import com.example.restaurant.repository.UserFoodLogRepository;
import com.example.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserFoodLogRepository userFoodLogRepository;
    private final KakaoApiService kakaoApiService;
    private final AIRecommendService aiRecommendService;

    /**
     * ✅ 1. 음식 추천 (AI 추천 + DB 로그 없는 경우 기본 추천)
     */
    public List<FoodRecommendationDto> recommendFoods(Long userId, String mood, String foodType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setMood(mood);
        long totalLogCount = userFoodLogRepository.count();

        // 1️⃣ DB 전체 로그가 없는 경우 (콜드 스타트)
        if (totalLogCount == 0) {
            System.out.println("⚠️ [시스템 콜드스타트] DB 로그 없음 → 기본 추천으로 대체");
            return basicRecommend(mood, foodType);
        }

        return aiRecommendService.getAIRecommendations(userId, mood, foodType);
    }

    /**
     * ✅ 2. 지역 + 음식 기반 맛집 추천 (동기 처리 + 로그 저장)
     */
    public List<Restaurant> getRestaurantsByRegionAndFood(Long userId, String region, String food) {

        String keyword = region + " " + food + " 맛집";
        String regionKey = keyword.replaceAll("\\s+", "");
        System.out.println("🍽️ 검색 키워드: " + keyword);

        // 🔥 1) 사용자 로그 저장 (동기 처리)
        try {
            User user = userRepository.findById(userId).orElseThrow();

            String foodType = userFoodLogRepository.findFirstByFoodName(food)
                    .map(UserFoodLog::getFoodType)
                    .orElse("기타");

            UserFoodLog log = UserFoodLog.builder()
                    .userId(user.getId())
                    .mood(user.getMood())
                    .foodName(food)
                    .foodType(foodType)
                    .region(region)
                    .createdAt(LocalDateTime.now())
                    .build();

            userFoodLogRepository.save(log);
            System.out.println("🧾 [로그] 사용자 행동 기록 저장 완료");

        } catch (Exception e) {
            System.out.println("⚠️ 로그 저장 실패: " + e.getMessage());
        }

        // 🔥 2) DB에 저장된 맛집 조회
        List<Restaurant> list =
                restaurantRepository.findByRegionContaining(regionKey);

        // 🔥 3) DB에 없으면 → Kakao API 동기 호출 (프론트에서 loading 유지함)
        if (list.isEmpty()) {
            try {
                System.out.println("🕓 데이터 없음 → Kakao API 수집 시작 (동기)");
                kakaoApiService.fetchAndSaveRestaurants(keyword);
            } catch (Exception e) {
                System.out.println("❌ Kakao 맛집 수집 실패: " + e.getMessage());
            }

            // 다시 DB 조회
            list = restaurantRepository.findByRegionContaining(regionKey);

            if (list.isEmpty()) {
                System.out.println("⚠️ Kakao API 완료 후에도 데이터 없음");
                return List.of();
            }
        }

        System.out.println("📦 [" + keyword + "] DB 로드: " + list.size() + "개");
        return list;
    }

    /**
     * ✅ 기본 추천 (DB 로그 없는 경우 fallback)
     */
    private List<FoodRecommendationDto> basicRecommend(String mood, String foodType) {

        List<String> foodNames = switch (foodType) {
            case "한식" -> mood.equals("행복") ? List.of("불고기", "비빔밥", "삼겹살")
                    : mood.equals("우울") ? List.of("순두부찌개", "된장찌개")
                    : mood.equals("스트레스") ? List.of("닭발", "떡볶이")
                    : List.of("김치찌개", "제육볶음");
            case "양식" -> List.of("스테이크", "피자", "파스타");
            case "중식" -> List.of("짜장면", "짬뽕", "탕수육");
            case "일식" -> List.of("초밥", "돈카츠", "우동");
            default -> List.of("치킨", "햄버거", "샌드위치");
        };

        List<FoodRecommendationDto> result = new ArrayList<>();
        for (String food : foodNames) {
            result.add(new FoodRecommendationDto(food, foodType));
        }

        return result;
    }
}
