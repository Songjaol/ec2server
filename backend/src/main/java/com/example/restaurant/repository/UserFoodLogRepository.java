package com.example.restaurant.repository;

import com.example.restaurant.entity.UserFoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserFoodLogRepository extends JpaRepository<UserFoodLog, Long> {
    List<UserFoodLog> findByUserId(Long userId);
    List<UserFoodLog> findByMood(String mood);
    @Query("SELECT f.foodName, COUNT(f) FROM UserFoodLog f WHERE f.mood = :mood GROUP BY f.foodName ORDER BY COUNT(f) DESC")
    List<Object[]> findPopularFoodsByMood(@Param("mood") String mood);
    Optional<UserFoodLog> findFirstByFoodName(String foodName);
    @Query("SELECT l.foodName, COUNT(l.foodName) AS cnt " +
            "FROM UserFoodLog l " +
            "WHERE l.foodType = :foodType " +
            "GROUP BY l.foodName " +
            "ORDER BY cnt DESC")
    List<Object[]> findPopularFoodsByFoodType(String foodType);


}
