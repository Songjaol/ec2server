package com.example.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterDto {

    @Schema(description = "유저 이름", example = "홍길동")
    private String name;

    @Schema(description = "유저 이메일", example = "test@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    private String password;

    @Schema(description = "선호 지역", example = "서울시 강남구")
    private String preferredArea;

    @Schema(description = "가격대", example = "₩₩")
    private String selectedPriceRange;

    @Schema(description = "선호 카테고리 목록", example = "[\"한식\", \"고기\"]")
    private List<String> selectedCategories;
}
