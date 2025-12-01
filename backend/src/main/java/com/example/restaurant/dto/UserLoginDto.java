package com.example.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UserLoginDto {

    @Schema(description = "로그인 이메일", example = "test@example.com")
    private String email;

    @Schema(description = "로그인 비밀번호", example = "1234")
    private String password;
}
