package com.example.restaurant.service;

import com.example.restaurant.dto.UserProfileDto;
import com.example.restaurant.dto.UserRegisterDto;
import com.example.restaurant.dto.UserResponseDto;
import com.example.restaurant.entity.User;
import com.example.restaurant.entity.UserRank;
import com.example.restaurant.repository.UserRankRepository;
import com.example.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRankRepository userRankRepository;

    // 회원가입
    public UserResponseDto register(UserRegisterDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        // 프로필 정보
        user.setRegion(dto.getPreferredArea());
        user.setPricePreference(dto.getSelectedPriceRange());
        user.setFavoriteFoodCategories(
                String.join(",", dto.getSelectedCategories())
        );

        // ⭐ 최초 XP = 0 → 랭크 자동 설정
        UserRank defaultRank = userRankRepository.findByXp(0);
        user.setRank(defaultRank);

        userRepository.save(user);

        return toResponse(user);
    }

    /**
     * 🔥 프로필 수정 (mood, food type)
     */
    public UserResponseDto updateProfile(Long userId, UserProfileDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setMood(dto.getMood());
        user.setFoodType(dto.getFoodType());

        // ⭐ 추가된 부분: region 저장
        if (dto.getRegion() != null && !dto.getRegion().isBlank()) {
            user.setRegion(dto.getRegion());
        }

        // ⭐ 랭크 갱신
        UserRank newRank = userRankRepository.findByXp(user.getXp());
        user.setRank(newRank);

        userRepository.save(user);

        return toResponse(user);
    }

    // 단일 조회
    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // XP 기준 랭크 재계산
        UserRank rank = userRankRepository.findByXp(user.getXp());
        user.setRank(rank);

        return toResponse(user);
    }

    // entity -> DTO 변환
    private UserResponseDto toResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mood(user.getMood())
                .foodType(user.getFoodType())
                .rankName(user.getRank() != null ? user.getRank().getRankName() : "Unranked")
                .rankDescription(user.getRank() != null ? user.getRank().getDescription() : "")
                .rankIcon(user.getRank() != null ? user.getRank().getIconUrl() : null)
                .xp(user.getXp())
                .build();
    }
}
