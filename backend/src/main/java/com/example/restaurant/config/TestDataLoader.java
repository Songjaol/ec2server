package com.example.restaurant.config;

import com.example.restaurant.entity.User;
import com.example.restaurant.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class TestDataLoader {

    private final UserRepository userRepository;

    private static final List<String> MOODS = List.of("행복", "우울", "스트레스", "피곤", "활기", "로맨틱", "편안", "신남");
    private static final List<String> FOOD_POOL = List.of(
            "한식", "중식", "일식", "양식", "패스트푸드", "분식", "기타", "디저트", "아시안"
    );
    private static final List<String> REGIONS = List.of("서울", "부산", "대구", "인천", "광주", "대전", "울산");
    private static final List<String> PRICE_PREF = List.of("저렴", "보통", "비쌈");

    @PostConstruct
    public void init() {
        if (userRepository.count() > 0) return;

        Random random = new Random();
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= 200; i++) {
            User user = new User();
            user.setName("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword("pass" + i);

            // 이전 코드:
// user.setPreferredArea(REGIONS.get(random.nextInt(REGIONS.size()))); ❌ setPreferredArea는 존재하지 않음
// user.setPriceRange(PRICE_PREF.get(random.nextInt(PRICE_PREF.size()))); ❌ setPriceRange는 존재하지 않음
// user.setFoodCategories(String.join(",", pickRandomSubset(FOOD_POOL, 3, random))); ❌ setFoodCategories는 존재하지 않음

// 수정 후 코드:
            user.setRegion(REGIONS.get(random.nextInt(REGIONS.size())));
            user.setPricePreference(PRICE_PREF.get(random.nextInt(PRICE_PREF.size())));
// setFavoriteFoodCategories는 List<String>을 받으므로, String.join 대신 List를 전달해야 함
            user.setFavoriteFoodCategories(
                    String.join(",", pickRandomSubset(FOOD_POOL, 3, random))
            );

            users.add(user);
        }

        userRepository.saveAll(users);
        System.out.println("✅ 테스트 데이터 삽입 완료: " + users.size() + "명 생성됨");
    }

    private List<String> pickRandomSubset(List<String> list, int count, Random random) {
        List<String> copy = new ArrayList<>(list);
        Collections.shuffle(copy, random);
        return copy.subList(0, count);
    }
}
