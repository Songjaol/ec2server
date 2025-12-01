import React, { useEffect, useState } from "react";
import api from "../api/axiosConfig";
import { useNavigate, useLocation } from "react-router-dom";

import profileImg from "../images/profile-1.png";
import homeIcon from "../images/home2.png";
import reviewsIcon from "../images/reviews.png";
import likesIcon from "../images/likes.png";
import trustIcon from "../images/trust.png";

// 메달 이미지
import medalBronze from "../images/bronze.png";
import medalSilver from "../images/sliver.png";
import medalGold from "../images/gold.png";
import medalDiamond from "../images/diamond.jpg"; // 플레 → 다이아 이미지로 사용
import restaurantIcon from "../images/restaurant.png";

export const ReviewProfile = () => {
  const [data, setData] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();

  // ⭐ 새로운 등급 기준
  const getRankByXp = (xp) => {
    if (xp < 200)
      return { name: "Unranked", level: 0, msg: "아직 활동이 없는 새내기", min: 0, max: 200 };

    if (xp < 500)
      return { name: "Bronze", level: 1, msg: "리뷰를 시작한 입문 리뷰어", min: 200, max: 500 };

    if (xp < 1000)
      return { name: "Silver", level: 2, msg: "꾸준히 활동하는 숙련 리뷰어", min: 500, max: 1000 };

    if (xp < 2000)
      return { name: "Gold", level: 3, msg: "신뢰받는 상위 리뷰어", min: 1000, max: 2000 };

    return { name: "Diamond", level: 4, msg: "전문성을 인정받은 최고 리뷰어", min: 2000, max: 2000 };
  };

  // ⭐ Rank별 메달 이미지
  const rankMedals = {
    Unranked: medalBronze,
    Bronze: medalBronze,
    Silver: medalSilver,
    Gold: medalGold,
    Diamond: medalDiamond,
  };

  // ⭐ Rank별 색상 테마
  const rankColors = {
    Unranked: "#fe9a00",
    Bronze: "#fe9a00",
    Silver: "#9aa3b1",
    Gold: "#e4c500",
    Diamond: "#8f4fff", // 플래티넘 색 유지
  };

  useEffect(() => {
    const fetchProfileData = async () => {
      try {
        const userRes = await api.get("/api/user/me");
        const userId = userRes.data.id;

        const profileRes = await api.get(`/api/review-profile/${userId}`);
        const profile = profileRes.data;

        const rank = getRankByXp(profile.xp);

        profile.rankName = rank.name;
        profile.level = rank.level;
        profile.rankMessage = rank.msg;
        profile.rankMin = rank.min;
        profile.rankMax = rank.max;

        setData(profile);
      } catch (err) {
        console.error("데이터 로딩 실패:", err);
        if (err.response && err.response.status === 401) {
          alert("로그인이 필요합니다.");
          navigate("/Login");
        }
      }
    };

    fetchProfileData();
  }, [location.key, navigate]);

  if (!data) {
    return <p className="text-center mt-10 text-lg font-semibold">프로필을 불러오는 중입니다...</p>;
  }

  const themeColor = rankColors[data.rankName];

  // XP 바 퍼센트
  const xpPercent =
    data.rankName === "Diamond"
      ? 100
      : Math.min(((data.xp - data.rankMin) / (data.rankMax - data.rankMin)) * 100, 100);

  const nextRankXp = data.rankName === "Diamond" ? 0 : data.rankMax - data.xp;

  const currentMedalImage = rankMedals[data.rankName];

  return (
    <div className="w-full min-h-screen bg-[#faf4e6] flex justify-center py-12 px-6 select-none">
      <div className="w-full max-w-[1100px]">

        {/* 홈으로 버튼 */}
        <div
          className="flex items-center gap-3 mb-10 cursor-pointer hover:opacity-80"
          onClick={() => navigate("/SelectFeel")}
        >
          <img src={homeIcon} alt="Home" className="w-8 h-8" />
          <span className="text-[20px] font-medium">홈으로</span>
        </div>

        {/* 상단 프로필 박스 */}
        <div className="w-full bg-[#fff3c8] rounded-2xl shadow p-8 flex flex-col md:flex-row gap-6 items-center">
          <img
            src={profileImg}
            alt="Profile"
            className="w-24 h-24 rounded-full shadow object-contain bg-white"
          />

          <div className="flex flex-col w-full">
            <div className="flex items-center gap-3 mb-1">
              <span className="text-[22px] font-semibold">{data.name}</span>

              <span
                className="text-white px-3 py-1 text-sm rounded-full font-medium"
                style={{ backgroundColor: themeColor }}
              >
                {data.rankName}
              </span>
            </div>

            <span className="text-[#6c6c6c] text-[15px] mt-1">
              {data.rankName} · {data.xp} XP
            </span>

            {/* XP Bar */}
            <div className="w-full bg-[#f0d8a8] h-3 rounded-full mt-3 overflow-hidden">
              <div
                className="h-full rounded-full transition-all"
                style={{ width: `${xpPercent}%`, backgroundColor: themeColor }}
              ></div>
            </div>

            <span className="text-[14px] mt-2 font-medium" style={{ color: themeColor }}>
              {data.rankName === "Diamond"
                ? "최고 등급에 도달했습니다!"
                : `다음 등급까지 ${nextRankXp} XP`}
            </span>
          </div>
        </div>

        {/* 통계 박스 */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-10">
          <div className="bg-white rounded-xl shadow p-8 text-center">
            <img src={reviewsIcon} className="w-16 h-16 mx-auto" />
            <p className="text-[32px] font-semibold mt-4">{data.reviewCount}</p>
            <p className="text-[#727272] mt-2">작성한 리뷰수</p>
          </div>

          <div className="bg-white rounded-xl shadow p-8 text-center">
            <img src={likesIcon} className="w-16 h-16 mx-auto" />
            <p className="text-[32px] font-semibold mt-4">{data.likeCount}</p>
            <p className="text-[#727272] mt-2">누른 좋아요</p>
          </div>

          <div className="bg-white rounded-xl shadow p-8 text-center">
            <img src={trustIcon} className="w-16 h-16 mx-auto" />
            <p className="text-[32px] font-semibold mt-4">{data.trustScore}%</p>
            <p className="text-[#727272] mt-2">신뢰도 점수</p>
          </div>
        </div>

        {/* 내가 간 식당 버튼 */}
        <div className="flex justify-center mt-10">
          <button
            onClick={() => navigate("/review-register")}
            className="w-full max-w-[1100px] bg-[#0057ff] hover:bg-[#0048d6] text-white py-4 
            rounded-xl shadow-lg flex items-center justify-center gap-3 text-[20px] font-medium"
          >
            <img src={restaurantIcon} className="w-7 h-7" />
            내가 간 식당 보기
          </button>
        </div>

        {/* 메달 목록 */}
        <div className="bg-white rounded-xl shadow p-6 mt-10">
          <div className="grid grid-cols-2 md:grid-cols-4 text-center gap-6">
            {["Bronze", "Silver", "Gold", "Diamond"].map((rank) => (
              <div
                key={rank}
                className={`transition-all ${
                  data.rankName === rank ? "opacity-100 scale-110" : "opacity-40 grayscale"
                }`}
              >
                <img src={rankMedals[rank]} className="mx-auto w-16 h-16" alt={rank} />
                <p className="mt-2 text-sm font-medium">{rank}</p>
              </div>
            ))}
          </div>
        </div>

        {/* 현재 등급 요약 */}
        <div
          className="rounded-xl shadow p-6 mt-10 flex items-center gap-4"
          style={{ backgroundColor: themeColor }}
        >
                    <div>
            <p className="text-lg font-bold text-white">{data.rankName} 등급</p>
            <p className="text-sm text-white/90">{data.rankMessage}</p>
          </div>
        </div>
      </div>
    </div>
  );
};
