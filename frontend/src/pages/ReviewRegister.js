import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axiosConfig";

export default function ReviewRegister() {
  const navigate = useNavigate();

  const [userId, setUserId] = useState(null);

  const [likedRestaurants, setLikedRestaurants] = useState([]);
  const [reviewedRestaurants, setReviewedRestaurants] = useState([]);

  const [selectedRestaurant, setSelectedRestaurant] = useState(null);

  const [rating, setRating] = useState(0);
  const [content, setContent] = useState("");
  const [visitedDate, setVisitedDate] = useState("");

  // 로그인된 유저 정보 불러오기
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await api.get("/api/user/me");
        setUserId(res.data.id);
      } catch {
        alert("로그인이 필요합니다.");
        navigate("/login");
      }
    };
    fetchUser();
  }, [navigate]);

  // 좋아요한 식당 목록
  useEffect(() => {
    if (!userId) return;
    api
      .get(`/api/review-likes/user/${userId}`)
      .then((res) => setLikedRestaurants(res.data))
      .catch((err) => console.error("❌ 좋아요 식당 로딩 실패:", err));
  }, [userId]);

  // 리뷰 작성한 식당 목록
  useEffect(() => {
    if (!userId) return;

    api
      .get(`/api/reviews/user/${userId}`)
      .then((res) =>
        setReviewedRestaurants(res.data.map((r) => r.restaurantId))
      )
      .catch((err) => console.error("❌ 리뷰 목록 로딩 실패:", err));
  }, [userId]);

  // 기존 리뷰 자동 로드
  const loadExistingReview = (restaurantId) => {
    api
      .get(`/api/reviews/user/${userId}/restaurant/${restaurantId}`)
      .then((res) => {
        const review = res.data;

        if (!review) {
          setRating(0);
          setContent("");
          setVisitedDate("");
          return;
        }

        setRating(review.rating);
        setContent(review.content);
        setVisitedDate(review.visitedDate);
      })
      .catch((err) => console.error("❌ 리뷰 로드 실패:", err));
  };

  // 리뷰 저장
  const handleSubmit = async () => {
    try {
      await api.post("/api/reviews", {
        userId,
        restaurantId: selectedRestaurant,
        rating,
        content,
        visitedDate,
      });

      alert("리뷰가 저장되었습니다! 🎉");

      setSelectedRestaurant(null);
    } catch (err) {
      console.error("❌ 리뷰 저장 실패:", err);
      alert(
        "리뷰 저장 중 오류 발생: " +
          (err.response?.data?.message || err.message)
      );
    }
  };

  // ────────────────────────────────────────
  // ⭐ 메인 레이아웃(깨짐 방지)
  // ────────────────────────────────────────
  return (
    <div className="min-h-screen bg-orange-50 py-8">
      <div className="max-w-4xl mx-auto p-6">

        {/* 상단 헤더는 항상 유지 → UI 깨짐 없음 */}
        <h1 className="text-2xl font-semibold mb-6 flex items-center gap-3">
          <div
            className="w-12 h-12 bg-orange-500 text-white flex justify-center items-center rounded-2xl cursor-pointer"
            onClick={() => navigate("/reviewprofile")}
          >
            🍽
          </div>
          내가 좋아요한 식당
        </h1>

        {/* 공통 카드 영역 */}
        <div className="bg-white p-8 rounded-2xl shadow">

          {/* ───────────────────────
              1) 리뷰 작성 화면
          ─────────────────────── */}
          {selectedRestaurant !== null ? (
            <>
              <button
                onClick={() => setSelectedRestaurant(null)}
                className="p-2 mb-6 hover:bg-gray-200"
              >
                ← 뒤로
              </button>

              <h2 className="text-xl font-semibold mb-4">
                {
                  likedRestaurants.find(
                    (l) => l.restaurantId === selectedRestaurant
                  )?.restaurantName
                }
              </h2>

              <div className="mb-4">
                <label>방문 날짜</label>
                <input
                  type="date"
                  className="border p-2 rounded w-full"
                  value={visitedDate}
                  onChange={(e) => setVisitedDate(e.target.value)}
                />
              </div>

              <div className="mb-4">
                <label>별점</label>
                <div className="flex gap-1 text-3xl">
                  {[1, 2, 3, 4, 5].map((n) => (
                    <span
                      key={n}
                      className="cursor-pointer"
                      onClick={() => setRating(n)}
                    >
                      {rating >= n ? "⭐" : "☆"}
                    </span>
                  ))}
                </div>
              </div>

              <div className="mb-4">
                <label>후기 내용</label>
                <textarea
                  rows="4"
                  className="border p-3 w-full rounded"
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                />
              </div>

              <button
                onClick={handleSubmit}
                className="w-full py-4 bg-orange-500 text-white rounded-xl"
              >
                저장하기
              </button>
            </>
          ) : (
            /* ───────────────────────
                2) 기본 화면 (식당 목록)
            ─────────────────────── */
            <>
              {likedRestaurants.length === 0 ? (
                <div className="text-center py-20 text-gray-600">
                  ❤️ 좋아요한 식당이 없습니다.
                </div>
              ) : (
                <div className="space-y-4">
                  {likedRestaurants.map((like) => (
                    <div
                      key={like.id}
                      className="border border-orange-200 p-6 rounded-xl shadow-sm"
                    >
                      <div className="flex justify-between items-center">
                        <div>
                          <h3 className="text-lg font-semibold">
                            {like.restaurantName}
                          </h3>

                          {reviewedRestaurants.includes(like.restaurantId) && (
                            <span className="text-sm bg-green-500 text-white px-3 py-1 rounded-full">
                              리뷰 완료
                            </span>
                          )}
                        </div>

                        <button
                          onClick={() => {
                            setSelectedRestaurant(like.restaurantId);
                            loadExistingReview(like.restaurantId);
                          }}
                          className="bg-blue-600 px-6 py-3 text-white rounded-xl"
                        >
                          {reviewedRestaurants.includes(like.restaurantId)
                            ? "리뷰 다시 쓰기"
                            : "리뷰 쓰기"}
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
