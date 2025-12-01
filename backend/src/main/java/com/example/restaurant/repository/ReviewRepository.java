package com.example.restaurant.repository;

import com.example.restaurant.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 리뷰 개수
    long countByUser_Id(Long userId);

    @Query("SELECT SUM(r.likes) FROM Review r WHERE r.user.id = :userId")
    Integer sumLikesByUser(@Param("userId") Long userId);

    List<Review> findByUser_Id(Long userId);

    Optional<Review> findByUser_IdAndRestaurant_Id(Long userId, Long restaurantId);


}


