package com.example.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "`user`")  // H2 예약어 충돌 방지
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    // =====================
    // Mood / Preference
    // =====================

    @Builder.Default
    private String mood = "";

    @Builder.Default
    private String foodType = "";

    private String region;
    private String pricePreference;

    // ⭐ 문자열(List → String) 그대로 유지
    private String favoriteFoodCategories;

    // =====================
    // Review Stats
    // =====================

    @Builder.Default
    private int level = 0;

    @Builder.Default
    private int xp = 0;

    @Builder.Default
    private int reviewCount = 0;

    @Builder.Default
    private int likeCount = 0;

    @Builder.Default
    private int trustScore = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_id")
    private UserRank rank;
}
