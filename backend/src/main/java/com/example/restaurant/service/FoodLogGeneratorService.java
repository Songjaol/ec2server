package com.example.restaurant.service;

import com.example.restaurant.entity.UserFoodLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodLogGeneratorService {

    private final ObjectMapper mapper;

    public FoodLogGeneratorService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());   // 🔥 LocalDateTime 지원
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 🔥 "2024-01-01 12:00:00" 가능
    }

    public List<UserFoodLog> parseJson(String json) {
        try {
            return mapper.readValue(
                    json,
                    mapper.getTypeFactory().constructCollectionType(List.class, UserFoodLog.class)
            );
        } catch (Exception e) {
            throw new RuntimeException("🔥 JSON 파싱 실패: " + e.getMessage());
        }
    }
}
