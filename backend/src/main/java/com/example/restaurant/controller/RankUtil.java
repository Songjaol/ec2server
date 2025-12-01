package com.example.restaurant.controller;

import com.example.restaurant.entity.UserRank;
import java.util.List;

/**
 * 유저 경험치(xp)에 따라 알맞은 UserRank 를 계산하는 유틸리티 클래스.
 */
public class RankUtil {

    /**
     * 주어진 경험치(xp)에 해당하는 랭크(UserRank)를 반환합니다.
     *
     * @param xp 유저의 총 경험치
     * @param ranks 전체 랭크 목록 (minXp ~ maxXp 범위를 가진 Rank 리스트)
     * @return 경험치가 속하는 UserRank, 해당하는 Rank 없으면 null
     */
    public static UserRank getRankByXp(int xp, List<UserRank> ranks) {
        return ranks.stream()
                .filter(r -> xp >= r.getMinXp() && xp <= r.getMaxXp())
                .findFirst()
                .orElse(null);
    }
}
