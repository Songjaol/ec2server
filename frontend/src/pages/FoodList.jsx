import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axiosConfig";

const FoodList = () => {
  const [foods, setFoods] = useState([]);
  const [loading, setLoading] = useState(true);

  const [mood, setMood] = useState(null);
  const [wantCategory, setWantCategory] = useState(null);
  const [visibleCount, setVisibleCount] = useState(4);

  const fetchUserProfile = async () => {
    try {
      const res = await api.get("/api/user/me");
      const user = res.data;

      setMood(user.mood || "행복");
      setWantCategory(user.foodType || "한식");

      // ⭐ region 저장
      if (user.region) {
        localStorage.setItem("region", user.region);
      }
    } catch (err) {
      console.error("❌ 유저 프로필 로딩 실패:", err);
    }
  };

  const fetchFoods = async (moodValue, categoryValue) => {
    try {
      const userId = localStorage.getItem("userId");
      const region = localStorage.getItem("region");

      const res = await api.get("/api/recommend/foods", {
        params: {
          userId: userId,
          mood: moodValue,
          foodType: categoryValue,
          region: region               // ← ★ region 필수
        }
      });

      let list = res.data;

      // Shuffle
      for (let i = list.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [list[i], list[j]] = [list[j], list[i]];
      }

      setFoods(list);
    } catch (error) {
      console.error("❌ 추천 불러오기 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUserProfile();
  }, []);

  useEffect(() => {
    if (mood && wantCategory) {
      fetchFoods(mood, wantCategory);
    }
  }, [mood, wantCategory]);

  return (
    <div className="w-full">
      {loading ? (
        <p className="text-gray-600 text-center text-xl my-10">
          추천 메뉴를 불러오는 중입니다...
        </p>
      ) : foods.length === 0 ? (
        <p className="text-gray-600 text-center text-xl my-10">
          추천 결과가 없습니다 😥
        </p>
      ) : (
        <>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            {foods.slice(0, visibleCount).map((food, idx) => (
              <Link key={idx} to={`/category/${food.foodType}`} className="block">
                <div className="bg-white rounded-2xl shadow-lg hover:shadow-xl transition p-6 flex flex-col items-center cursor-pointer">
                  <img
                    src={food.imageUrl}
                    alt={food.name}
                    className="w-20 h-20 object-contain mb-3 rounded-lg"
                  />
                  <p className="text-lg font-semibold text-gray-800">
                    {food.name}
                  </p>
                </div>
              </Link>
            ))}
          </div>

          {visibleCount < foods.length && (
            <div className="flex justify-center mt-10">
              <button
                onClick={() => setVisibleCount(prev => prev + 4)}
                className="px-6 py-3 bg-[#ffae00] hover:bg-[#e69c00] text-white font-semibold rounded-xl shadow transition active:scale-95"
              >
                더 보기
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default FoodList;
