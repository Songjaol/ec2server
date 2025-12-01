import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/axiosConfig";

export default function CategoryPage() {
  const { foodType } = useParams();
  const navigate = useNavigate();

  const [restaurants, setRestaurants] = useState([]);
  const [index, setIndex] = useState(0);
  const [isFlipped, setIsFlipped] = useState(false);
  const [loading, setLoading] = useState(true);

  const sampleReviews = [
    "여기 진짜 맛있어요! 또 오고 싶어요 😊",
    "양도 많고 가격도 괜찮아요!",
    "직원분들이 너무 친절했습니다.",
    "가게 분위기가 좋아요!",
    "기대했던 것보다 훨씬 맛있어요!",
    "조금 짰지만 전체적으로 만족!",
    "웨이팅 있었지만 금방 들어갔어요.",
    "재료가 신선해서 좋았어요.",
    "평범했지만 괜찮았어요.",
    "맛있지만 가격이 살짝 비싸요.",
    "다음에 또 방문할 의향 있어요!",
    "메뉴가 다양해서 선택 폭이 넓어요.",
    "사진보다 훨씬 맛있어요!",
    "포장도 잘 해주시고 맛도 좋아요.",
    "혼밥하기 딱 좋아요!",
    "매운맛 lovers 강추🔥",
    "가성비 최고!",
    "데이트 코스로 강력 추천 ❤️",
    "단골집 예약!",
    "친구랑 가기 좋아요!"
  ];

  const [randomReviews, setRandomReviews] = useState([]);
  const [likedReviews, setLikedReviews] = useState([false, false, false]);
  const [restaurantLiked, setRestaurantLiked] = useState(false);

  /* 🔥 맛집 추천 API */
  const fetchRestaurants = async () => {
    try {
      const userRes = await api.get("/api/user/me");
      const userId = userRes.data.id;
      const region = userRes.data.region ?? "강남";
      const mood = userRes.data.mood ?? "기본";

      const res = await api.get("/api/recommend/restaurants", {
        params: { userId, region, food: foodType, mood },
      });

      setRestaurants(res.data);
      setIndex(0);
    } catch (err) {
      console.error("❌ 맛집 불러오기 실패:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRestaurants();
  }, [foodType]);

  /* 카드 변경 → 리뷰 갱신 */
  useEffect(() => {
    if (!restaurants[index]) return;

    const shuffled = [...sampleReviews].sort(() => 0.5 - Math.random());
    setRandomReviews(shuffled.slice(0, 3));
    setLikedReviews([false, false, false]);
    setRestaurantLiked(false);
  }, [index, restaurants]);

  const toggleLike = async (idx) => {
    const user = await api.get("/api/user/me");
    const userId = user.data.id;
    const restaurantId = restaurants[index].id;

    const updated = [...likedReviews];
    updated[idx] = !updated[idx];
    setLikedReviews(updated);

    if (restaurantLiked) return;

    try {
      await api.post("/api/review-likes", {
        userId,
        restaurantId,
        reviewText: randomReviews[idx],
        likedIndex: idx,
      });
      setRestaurantLiked(true);
    } catch (err) {
      console.error("❌ 좋아요 저장 실패:", err);
    }
  };

  const goNext = () => {
    if (index < restaurants.length - 1) {
      setIsFlipped(false);
      setIndex((prev) => prev + 1);
    }
  };

  const goPrev = () => {
    if (index > 0) {
      setIsFlipped(false);
      setIndex((prev) => prev - 1);
    }
  };

  const current = restaurants[index];
  const prevCard = index > 0 ? restaurants[index - 1] : null;
  const nextCard = index < restaurants.length - 1 ? restaurants[index + 1] : null;

  return (
    <div className="bg-[#F7F3E7] min-h-screen py-10 relative">

      {/* 뒤로가기 */}
      <button
        onClick={() => navigate(-1)}
        className="fixed top-6 left-6 px-4 py-2 bg-[#C8B28A] text-white rounded-xl shadow z-50"
      >
        ← 뒤로가기
      </button>

      {/* 헤더 */}
      <div className="mx-auto w-[90%] md:w-[80%] bg-[#E6D2A9] py-8 rounded-3xl text-center shadow-md">
        <h1 className="text-4xl font-bold text-[#6D5535] mb-2">
          {foodType} 맛집 추천!
        </h1>
        <p className="text-lg text-gray-700">
          감정과 취향에 맞는 최적의 맛집을 준비했어요.
        </p>
      </div>

      {/* 로딩 */}
      {loading && <div className="text-center text-xl py-20">로딩 중...</div>}

      {/* 데이터 없음 */}
      {!loading && restaurants.length === 0 && (
        <div className="text-center text-xl py-20 text-gray-600">
          😥 {foodType} 관련 맛집을 찾지 못했습니다.
        </div>
      )}

      {!loading && restaurants.length > 0 && (
        <div className="mt-10 flex justify-center gap-8 items-center">

          {/* 이전 카드 */}
          {prevCard && (
            <div className="w-[260px] opacity-40 scale-90 blur-[1px]">
              <img src={prevCard.imageUrl} className="w-full h-44 object-cover rounded-t-2xl" />
              <div className="bg-white rounded-b-2xl p-3 text-center text-sm">
                {prevCard.name}
              </div>
            </div>
          )}

          {/* 현재 카드 */}
          <div className="w-[380px] min-h-[500px]" style={{ perspective: "1000px" }}>
            <div
              className="w-full h-full transition-transform duration-500"
              style={{
                transformStyle: "preserve-3d",
                transform: isFlipped ? "rotateY(180deg)" : "rotateY(0deg)",
              }}
            >

              {/* 앞면 */}
              <div className="absolute w-full min-h-[500px] bg-white rounded-3xl shadow-xl overflow-hidden"
                style={{ backfaceVisibility: "hidden" }}>
                <img src={current.imageUrl} className="w-full h-56 object-cover" />

                <div className="p-6">
                  <h2 className="text-2xl font-bold mb-1 text-[#5A4530]">{current.name}</h2>
                  <p className="text-gray-600 text-sm mb-4">{current.address}</p>

                  <button
                    onClick={() => window.open(current.placeUrl || current.mapUrl, "_blank")}
                    className="w-full mb-3 bg-[#6D5535] text-white py-3 rounded-xl"
                  >
                    📍 상세 보기
                  </button>

                  <button
                    className="w-full bg-[#A58963] text-white py-3 rounded-xl"
                    onClick={() => setIsFlipped(true)}
                  >
                    리뷰 보기 (R)
                  </button>
                </div>
              </div>

              {/* 뒷면 */}
              <div className="absolute w-full min-h-[500px] bg-white rounded-3xl shadow-xl p-6"
                style={{ transform: "rotateY(180deg)", backfaceVisibility: "hidden" }}>
                <h2 className="text-2xl font-bold text-[#5A4530] mb-4">리뷰</h2>

                <ul className="space-y-4">
                  {randomReviews.map((review, idx) => (
                    <li key={idx} className="flex justify-between items-center">
                      <span>• {review}</span>
                      <button
                        onClick={() => toggleLike(idx)}
                        className="text-2xl select-none"
                      >
                        {likedReviews[idx] ? "❤️" : "🤍"}
                      </button>
                    </li>
                  ))}
                </ul>

                <button
                  className="w-full mt-5 bg-[#A58963] text-white py-3 rounded-xl"
                  onClick={() => setIsFlipped(false)}
                >
                  돌아가기
                </button>
              </div>
            </div>
          </div>

          {/* ⭐ 다음 카드 */}
{nextCard && (
  <div
    className={`
      w-[260px] opacity-40 scale-90 blur-[1px] transition-all
      ${index === 0 ? "translate-x-20" : ""}
    `}
  >
    <img
      src={nextCard.imageUrl}
      className="w-full h-44 object-cover rounded-t-2xl"
    />
    <div className="bg-white rounded-b-2xl p-3 text-center text-sm">
      {nextCard.name}
    </div>
  </div>
)}


        </div>
      )}

      {/* 이전 / 다음 버튼 */}
      {!loading && restaurants.length > 0 && (
        <div className="flex justify-center gap-10 mt-10 mb-10">
          <button
            onClick={goPrev}
            className="px-6 py-3 bg-[#E6DCC7] text-[#6D5535] rounded-xl shadow"
          >
            ← 이전
          </button>
          <button
            onClick={goNext}
            className="px-6 py-3 bg-[#E6DCC7] text-[#6D5535] rounded-xl shadow"
          >
            다음 →
          </button>
        </div>
      )}

    </div>
  );
}
