package com.example.restaurant.repository;

import com.example.restaurant.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<ReviewLike, Long> {
    long countByUser_Id(Long userId);
}
