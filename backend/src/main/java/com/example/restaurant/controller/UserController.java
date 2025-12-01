package com.example.restaurant.controller;

import com.example.restaurant.config.JWTUtil;
import com.example.restaurant.dto.UserLoginDto;
import com.example.restaurant.dto.UserProfileDto;
import com.example.restaurant.dto.UserRegisterDto;
import com.example.restaurant.dto.UserResponseDto;
import com.example.restaurant.entity.User;
import com.example.restaurant.entity.UserRank;
import com.example.restaurant.repository.UserRankRepository;
import com.example.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "회원가입, 로그인, 프로필 수정, 내 정보 조회 API")  // ⭐ 문서화 추가
public class UserController {

    private final UserRepository userRepository;
    private final UserRankRepository userRankRepository;
    private final PasswordEncoder passwordEncoder;

    // =============================
    // 회원가입
    // =============================
    @Operation(
            summary = "회원가입",
            description = "회원가입을 진행하고, 성공 시 JWT 토큰을 발급합니다."
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody UserRegisterDto dto
    ) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("이미 가입된 이메일입니다.");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());

        // 프로필 정보
        user.setRegion(dto.getPreferredArea());
        user.setPricePreference(dto.getSelectedPriceRange());
        user.setFavoriteFoodCategories(
                String.join(",", dto.getSelectedCategories())
        );

        // 최초 XP = 0 → UNRANKED 저장
        UserRank defaultRank = userRankRepository.findByRankName("Unranked")
                .orElse(null);
        user.setRank(defaultRank);

        User saved = userRepository.save(user);

        String token = JWTUtil.createJwt(saved.getEmail(), saved.getId());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", saved.getId()
        ));
    }

    // =============================
    // 로그인
    // =============================
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다."
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody UserLoginDto dto
    ) {

        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = JWTUtil.createJwt(user.getEmail(), user.getId());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", user.getId()
        ));
    }

    // =============================
    // 프로필 수정
    // =============================
    @Operation(
            summary = "프로필 수정",
            description = "유저의 기분, 음식 타입, 지역 등을 수정합니다. JWT가 필요합니다."
    )
    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @Parameter(description = "Bearer JWT 토큰") @RequestHeader("Authorization") String authHeader,
            @RequestBody UserProfileDto dto
    ) {
        Long userId = JWTUtil.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setMood(dto.getMood());
        user.setFoodType(dto.getFoodType());

        if (dto.getRegion() != null && !dto.getRegion().isBlank()) {
            user.setRegion(dto.getRegion());
        }

        User saved = userRepository.save(user);
        return ResponseEntity.ok(toUserResponse(saved));
    }

    // =============================
    // 내 정보 조회
    // =============================
    @Operation(
            summary = "내 정보 조회",
            description = "JWT 토큰 기반으로 현재 로그인한 사용자의 정보를 반환합니다."
    )
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @Parameter(description = "Bearer JWT 토큰")
            @RequestHeader("Authorization") String authHeader
    ) {

        Long userId = JWTUtil.getUserIdFromToken(authHeader.replace("Bearer ", ""));

        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(toUserResponse(user)))
                .orElse(ResponseEntity.status(404).build());
    }

    // =============================
    // User → UserResponseDto 변환
    // =============================
    private UserResponseDto toUserResponse(User user) {

        UserRank rank = user.getRank();

        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mood(user.getMood())
                .foodType(user.getFoodType())
                .region(user.getRegion())
                .rankName(rank != null ? rank.getRankName() : "Unranked")
                .rankDescription(rank != null ? rank.getDescription() : "활동을 시작해보세요!")
                .rankIcon(rank != null ? rank.getIconUrl() : null)
                .build();
    }

}
