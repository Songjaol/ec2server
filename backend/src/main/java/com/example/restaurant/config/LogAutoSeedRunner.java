package com.example.restaurant.config;

import com.example.restaurant.entity.UserFoodLog;
import com.example.restaurant.repository.UserFoodLogRepository;
import com.example.restaurant.service.FoodLogGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LogAutoSeedRunner {

    private final FoodLogGeneratorService generatorService;
    private final UserFoodLogRepository foodLogRepository;

    @Bean
    public ApplicationRunner autoSeedLogs() {
        return args -> {

            long count = foodLogRepository.count();

            if (count >= 1000) {
                log.info("🍀 기존 로그 {}건 존재 → 자동생성 스킵", count);
                return;
            }

            // 1) 파일 읽기
            String json = Files.readString(Path.of("food_logs.json"));

            // 2) JSON → 객체 변환
            List<UserFoodLog> logs = generatorService.parseJson(json);

            // 3) 그대로 DB 저장 (userId 검증 제거)
            foodLogRepository.saveAll(logs);

            log.info("✅ {}개 로그 저장 완료!", logs.size());
        };
    }
}
