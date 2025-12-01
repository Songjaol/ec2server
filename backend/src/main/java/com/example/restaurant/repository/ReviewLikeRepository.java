package com.example.restaurant.repository;

import com.example.restaurant.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    boolean existsByUser_IdAndRestaurant_Id(Long userId, Long restaurantId);

    void deleteByUser_IdAndRestaurant_Id(Long userId, Long restaurantId);

    List<ReviewLike> findByUser_Id(Long userId);

    @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.user.id = :userId")
    long countLikesByUserId(@Param("userId") Long userId);

    // ❗❗❗ 완전 삭제해야 함
    // List<Long> findRestaurantIdsByUserId(Long userId);
}

