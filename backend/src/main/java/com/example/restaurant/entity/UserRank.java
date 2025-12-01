package com.example.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rankName;    // Bronze / Silver / Gold / Platinum
    private String description; // 등급 설명

    private int minXp;          // 등급 최소 XP
    private int maxXp;          // 등급 최대 XP

    private String iconUrl;     // 아이콘 URL 또는 이미지 이름 (선택)
}
