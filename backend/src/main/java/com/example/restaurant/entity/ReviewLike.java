package com.example.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    private String reviewText; // 랜덤 리뷰 선택 시 저장 가능 (원하면 제거 가능)

    private int likedIndex; // 0,1,2 중 어떤 리뷰를 좋아요 눌렀는지 저장 가능

    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
}
