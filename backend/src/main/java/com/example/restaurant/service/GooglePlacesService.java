package com.example.restaurant.service;

import com.example.restaurant.entity.Restaurant;
import com.example.restaurant.repository.RestaurantRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;   // ⭐ Spring HttpHeaders
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GooglePlacesService {

    @Value("${api.google.key}")
    private String googleApiKey;

    private final RestaurantRepository restaurantRepository;

    private static final String GOOGLE_SEARCH_URL =
            "https://places.googleapis.com/v1/places:searchText";


    /**
     * ⭐ Google Places에서 맛집 데이터 수집 + DB 저장
     */
    public void fetchAndSaveRestaurants(String keyword) {
        try {
            List<Restaurant> list = fetchAndReturnRestaurants(keyword);

            if (!list.isEmpty()) {
                restaurantRepository.saveAll(list);
                System.out.println("🍽 Google 저장 완료: " + list.size() + "개");
            }

        } catch (Exception e) {
            System.out.println("❌ Google Places 오류: " + e.getMessage());
        }
    }


    /**
     * ⭐ Google Places 호출 후 Restaurant 리스트를 반환
     * RecommendService가 그냥 이걸 쓰면 됨
     */
    public List<Restaurant> fetchAndReturnRestaurants(String keyword) {

        List<Restaurant> newRestaurants = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            // 🔥 Google Places 요청 body 구성
            ObjectNode body = mapper.createObjectNode();
            body.put("textQuery", keyword);
            body.put("languageCode", "ko");
            body.put("regionCode", "KR");

            String requestBody = mapper.writeValueAsString(body);

            // 🔥 여기가 문제였던 import 해결됨
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Goog-Api-Key", googleApiKey);
            headers.add("X-Goog-FieldMask",
                    "places.displayName,places.formattedAddress,"
                            + "places.location,places.id,places.photos");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            RestTemplate rest = new RestTemplate();

            // 🔥 API 요청
            ResponseEntity<String> response = rest.exchange(
                    GOOGLE_SEARCH_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = mapper.readTree(response.getBody());
            JsonNode places = root.path("places");

            if (!places.isArray() || places.size() == 0) {
                System.out.println("❌ Google: 검색 결과 없음");
                return List.of();
            }

            // 🔥 Google Places 결과 반복 처리
            for (JsonNode place : places) {

                String name = place.path("displayName").path("text").asText();
                String address = place.path("formattedAddress").asText();

                // DB 중복 체크
                if (restaurantRepository.existsByNameAndAddress(name, address))
                    continue;

                double lat = place.path("location").path("latitude").asDouble();
                double lng = place.path("location").path("longitude").asDouble();
                String placeId = place.path("id").asText();

                String photoUrl = extractGooglePhotoUrl(place);

                Restaurant r = Restaurant.builder()
                        .name(name)
                        .address(address)
                        .x(lng)
                        .y(lat)
                        .region(keyword.replaceAll("\\s+", ""))
                        .kakaoId(placeId)   // 필드명은 kakaoId지만 placeId 저장
                        .imageUrl(photoUrl)
                        .build();

                newRestaurants.add(r);
            }

        } catch (Exception e) {
            System.out.println("❌ Google API 파싱 오류: " + e.getMessage());
        }

        return newRestaurants;
    }


    /**
     * ⭐ Google Photo URL 생성
     */
    private String extractGooglePhotoUrl(JsonNode place) {

        JsonNode photos = place.path("photos");

        if (!photos.isArray() || photos.size() == 0) {
            return "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800";
        }

        String photoName = photos.get(0).path("name").asText();

        return "https://places.googleapis.com/v1/" + photoName +
                "/media?maxWidthPx=1200&key=" + googleApiKey;
    }
}
