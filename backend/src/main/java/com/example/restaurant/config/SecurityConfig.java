package com.example.restaurant.config;
// 1. (신규) CORS 관련 클래스 임포트
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

// (기존 임포트)
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 2. (신규) CORS 설정을 SecurityFilterChain에 추가
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 3. CSRF 비활성화 (JWT를 사용하므로 stateless)
            .csrf(csrf -> csrf.disable())

            // 4. 세션 관리 비활성화 (stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 5. HTTP 요청 권한 설정
            .authorizeHttpRequests(authorize -> authorize
                // ✅ /api/user/register 와 /api/user/login 은 누구나 접근 허용
                // (이 설정이 302 Found 오류를 해결합니다)
                .requestMatchers("/api/user/register", "/api/user/login", "/api/review-profile/**").permitAll() 
                
                // (참고: /me, /profile 등은 나중에 .authenticated()로 변경해야 합니다)
                .requestMatchers("/api/user/me", "/api/user/profile", "/api/review-profile/**").permitAll() 
                
                // 그 외 모든 요청도 일단 허용 (개발 초기 단계)
                .anyRequest().permitAll() 
            );

        return http.build();
    }

    // 6. (신규) CORS 설정을 위한 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 7. React 앱의 주소(localhost:3000)를 허용
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        
        // 8. 허용할 HTTP 메서드 (OPTIONS 포함)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 9. 허용할 HTTP 헤더 (Authorization 헤더 등)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 대해 위에서 정의한 CORS 설정 적용
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 해시 알고리즘을 사용하는 PasswordEncoder를 반환
        return new BCryptPasswordEncoder();
    }
}