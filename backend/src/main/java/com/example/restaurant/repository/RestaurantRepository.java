package com.example.restaurant.repository;

import com.example.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 식당 중복 저장 방지용
    boolean existsByNameAndAddress(String name, String address);

    // 지역 + 음식 종류 기반 추천 검색
    @Query("SELECT r FROM Restaurant r " +
            "WHERE REPLACE(r.region, ' ', '') LIKE CONCAT('%', :normalizedRegion, '%') " +
            "AND r.category LIKE CONCAT('%', :food, '%')")
    List<Restaurant> searchRestaurants(
            @Param("normalizedRegion") String normalizedRegion,
            @Param("food") String food
    );
    List<Restaurant> findByRegionContaining(String region);

}

