package com.example.restaurant.repository;

import com.example.restaurant.entity.UserRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRankRepository extends JpaRepository<UserRank, Long> {

    @Query("SELECT r FROM UserRank r WHERE r.minXp <= :xp AND r.maxXp >= :xp")
    UserRank findByXp(@Param("xp") int xp);

    Optional<UserRank> findByRankName(String rankName);
}
