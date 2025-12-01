package com.example.restaurant.service;

import com.example.restaurant.entity.Restaurant;
import com.example.restaurant.repository.RestaurantRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KakaoApiService {

    private final RestaurantRepository restaurantRepository;

    @Value("${api.kakao.key}")
    private String kakaoApiKey;

    @Value("${api.naver.client-id}")
    private String naverClientId;

    @Value("${api.naver.client-secret}")
    private String naverClientSecret;

    @Value("${api.google.key}")
    private String googleApiKey;

    private static final String KAKAO_API_URL =
            "https://dapi.kakao.com/v2/local/search/keyword.json";

    /** ⭐ 지역 기반 맛집 수집 (로딩 흐름 지원) */
    public void fetchAndSaveRestaurants(String keyword) {

        System.out.println("⏳ [Kakao] '" + keyword + "' 수집 시작...");

        synchronized (this) {
            try {

                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();

                // 🔥 Kakao API 호출
                List<Restaurant> fetched = fetchFromKakao(keyword, client, mapper);

                System.out.println("📩 [Kakao] 응답 수: " + fetched.size());

                if (fetched.isEmpty()) {
                    System.out.println("⚠️ [" + keyword + "] Kakao 결과 없음");
                    return;
                }

                // 🔥 중복 제거
                Map<String, Restaurant> uniqueMap = new LinkedHashMap<>();
                for (Restaurant r : fetched) {
                    uniqueMap.putIfAbsent(r.getName() + "_" + r.getAddress(), r);
                }

                List<Restaurant> unique = new ArrayList<>(uniqueMap.values());
                List<Restaurant> newOnes = unique.stream()
                        .filter(r -> !restaurantRepository.existsByNameAndAddress(
                                r.getName(), r.getAddress()))
                        .toList();

                if (!newOnes.isEmpty()) {
                    restaurantRepository.saveAll(newOnes);
                    System.out.println("🍽 [" + keyword + "] 새 데이터 " + newOnes.size() + "개 저장됨");
                } else {
                    System.out.println("ℹ️ [" + keyword + "] 저장할 신규 없음");
                }

            } catch (Exception e) {
                System.out.println("❌ [Kakao] 수집 실패: " + e.getMessage());
                throw new RuntimeException("Kakao API 오류", e);
            } finally {
                System.out.println("✅ [Kakao] '" + keyword + "' 수집 종료");
            }
        }
    }

    /** ⭐ Kakao API 호출 */
    private List<Restaurant> fetchFromKakao(String query, HttpClient client, ObjectMapper mapper) {

        System.out.println("➡️ [Kakao] 요청 시작: " + query);

        List<Restaurant> list = new ArrayList<>();

        try {
            String url = KAKAO_API_URL + "?query=" +
                    URLEncoder.encode(query, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", kakaoApiKey)
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode docs = mapper.readTree(response.body()).path("documents");

            if (!docs.isArray() || docs.size() == 0) return list;

            for (JsonNode node : docs) {

                String name = node.path("place_name").asText();
                String address = node.path("address_name").asText();

                if (restaurantRepository.existsByNameAndAddress(name, address)) continue;

                Restaurant r = Restaurant.builder()
                        .name(name)
                        .category(node.path("category_name").asText())
                        .address(address)
                        .phone(node.path("phone").asText(""))
                        .x(node.path("x").asDouble())
                        .y(node.path("y").asDouble())
                        .region(query.replaceAll("\\s+", ""))
                        .placeUrl(node.path("place_url").asText())
                        .imageUrl(fetchImageWithFallback(name))
                        .build();

                list.add(r);
            }

        } catch (Exception e) {
            System.out.println("❌ [Kakao] fetch 실패: " + e.getMessage());
            throw new RuntimeException("Kakao fetch 실패", e);
        }

        System.out.println("⬅️ [Kakao] 응답 처리 완료");
        return list;
    }

    /** 이미지 검색 (Google → Naver → 기본) */
    private String fetchImageWithFallback(String name) {
        String img = fetchImageFromGoogle(name);
        if (img != null) return img;
        img = fetchImageFromNaver(name);
        if (img != null) return img;
        return "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800";
    }

    private String fetchImageFromGoogle(String placeName) {
        try {
            // ✅ 1. API 엔드포인트
            String url = "https://places.googleapis.com/v1/places:searchText";

            // ✅ 2. 요청 본문 — 상호명 단위 검색
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode body = mapper.createObjectNode();
            body.put("textQuery", placeName); // 예: "감성타코 홍대점"
            body.put("languageCode", "ko");
            body.put("regionCode", "KR");

            String requestBody = mapper.writeValueAsString(body);

            // ✅ 3. HTTP 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Goog-Api-Key", googleApiKey);
            headers.add("X-Goog-FieldMask", "places.photos,places.displayName");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();

            // ✅ 4. Google Places Text Search 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            System.out.println(response.getBody());
            // ✅ 5. 응답 파싱
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode places = root.path("places");

            if (!places.isArray() || places.isEmpty()) {
                System.out.println("⚠️ Google API: [" + placeName + "] 검색 결과 없음");
                return null;
            }

            // ✅ 6. 첫 번째 장소의 사진 정보 가져오기
            JsonNode first = places.get(0);
            JsonNode photos = first.path("photos");

            if (photos.isArray() && photos.size() > 0) {
                String photoName = photos.get(0).path("name").asText();

                // ✅ 7. 최종 이미지 URL 반환 (인코딩 제거)
                String photoUrl = "https://places.googleapis.com/v1/" + photoName +
                        "/media?maxWidthPx=800&key=" + googleApiKey;

                return photoUrl;
            }

        } catch (Exception e) {
            System.out.println("⚠️ Google Places(New) 요청 실패: " + e.getMessage());
        }
        return null;
    }



    public String fetchImageFromNaver(String name) {
        try {
            String url = "https://openapi.naver.com/v1/search/image?query=" +
                    URLEncoder.encode(name + " 음식 메뉴", StandardCharsets.UTF_8) +
                    "&display=10&sort=sim";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", naverClientId);
            headers.set("X-Naver-Client-Secret", naverClientSecret);

            RestTemplate rest = new RestTemplate();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response =
                    rest.exchange(url, HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode items = mapper.readTree(response.getBody()).path("items");

            List<String> validImages = new ArrayList<>();

            for (JsonNode item : items) {
                String link = item.path("link").asText();

                // ✅ 확장자 / 도메인 필터링 강화
                boolean isValidExt = link.matches(".*\\.(jpg|jpeg|png|webp)$");
                boolean isSafeDomain = !(link.contains("blog")
                        || link.contains("shopping")
                        || link.contains("kin.naver.com")
                        || link.contains("ssl.pstatic.net")
                        || link.contains("news.naver.com"));

                if (isValidExt && isSafeDomain) {
                    validImages.add(link);
                }
            }

            // ✅ 랜덤 선택 (결과 다양화)
            if (!validImages.isEmpty()) {
                return validImages.get(new Random().nextInt(validImages.size()));
            }

        } catch (Exception e) {
            System.out.println("⚠️ Naver 이미지 검색 실패: " + e.getMessage());
        }

        // ✅ fallback 이미지
        return "https://images.unsplash.com/photo-1600891964599-f61ba0e24092?w=800";
    }

}