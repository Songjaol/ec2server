import React, { useState, useEffect } from "react";
import api from "../api/axiosConfig";

import image5 from "../images/image-5.png";
import image17 from "../images/image-17.png";
import image18 from "../images/image-18.png";
import image19 from "../images/image-19.png";
import image20 from "../images/image-20.png";
import image1 from "../images/image-1.png";

import vector6 from "../images/vector-6.svg";
import vector7 from "../images/vector-7.svg";
import vector8 from "../images/vector-8.svg";
import vector9 from "../images/vector-9.svg";
import vector10 from "../images/vector-10.svg";
import vector11 from "../images/vector-11.svg";
import vector12 from "../images/vector-12.svg";
import vector13 from "../images/vector-13.svg";

// 🔥 프로필 이미지 추가 (images/user.png 예시)
import userImage from "../images/Profile.png";

import { useNavigate } from "react-router-dom";

export const SelectFeel = () => {
  const navigate = useNavigate();

  const [region, setRegion] = useState("");
  const [selectedMood, setSelectedMood] = useState(null);
  const [selectedFood, setSelectedFood] = useState(null);
  const [userName, setUserName] = useState("");

  /** ⭐ 옵션 데이터 */
  const moodOptions = [
    { id: 1, label: "행복", bgColor: "bg-yellow-100", borderColor: "border-yellow-300", icon: vector6 },
    { id: 2, label: "우울", bgColor: "bg-blue-100", borderColor: "border-blue-300", icon: vector7 },
    { id: 3, label: "스트레스", bgColor: "bg-red-100", borderColor: "border-red-300", icon: vector8 },
    { id: 4, label: "피곤", bgColor: "bg-gray-100", borderColor: "border-gray-300", icon: vector9 },
    { id: 5, label: "활기차요", bgColor: "bg-orange-100", borderColor: "border-orange-300", icon: vector10 },
    { id: 6, label: "로맨틱", bgColor: "bg-pink-100", borderColor: "border-pink-300", icon: vector11 },
    { id: 7, label: "편안해요", bgColor: "bg-green-100", borderColor: "border-green-300", icon: vector12 },
    { id: 8, label: "신나요!", bgColor: "bg-purple-100", borderColor: "border-purple-300", icon: vector13 },
  ];

  const foodOptions = [
    { id: 1, label: "한식", bgColor: "bg-yellow-100", borderColor: "border-yellow-300", image: image17 },
    { id: 2, label: "중식", bgColor: "bg-pink-100", borderColor: "border-pink-300", image: image18 },
    { id: 3, label: "일식", bgColor: "bg-red-100", borderColor: "border-red-300", image: image19 },
    { id: 4, label: "양식", bgColor: "bg-green-100", borderColor: "border-green-300", image: image20 },
    { id: 5, label: "디저트", bgColor: "bg-yellow-100", borderColor: "border-yellow-300", image: image1 },
  ];

  /** ⭐ 유저 정보 불러오기 */
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await api.get("/api/user/me");
        setUserName(res.data.name);
      } catch (err) {
        console.error("유저 정보 불러오기 실패:", err);
      }
    };
    fetchUser();
  }, []);

  /** ⭐ 저장 버튼 실행 */
  const handleSaveAndNext = async () => {
  const moodLabel = moodOptions.find((m) => m.id === selectedMood)?.label;
  const foodLabel = foodOptions.find((f) => f.id === selectedFood)?.label;

  if (!region || !moodLabel || !foodLabel) {
    alert("지역 / 기분 / 음식 모두 선택해주세요!");
    return;
  }

  try {
    await api.post("/api/user/profile", {
      mood: moodLabel,
      foodType: foodLabel,
      region: region,
    });

    console.log("✔ 프로필 저장 완료!");

    // 🔥 foodType을 반드시 전달해야 함
    navigate(
      `/Menu?region=${encodeURIComponent(region)}&foodType=${encodeURIComponent(foodLabel)}`
    );

  } catch (err) {
    console.error("❌ 프로필 저장 실패:", err);
  }
};


  const handleProfile = () => {
    navigate("/review-profile");
  };

  return (
    <div className="min-h-screen w-full bg-[#faf5e9]">

      {/* 🔥 헤더 */}
      <header className="w-full h-[90px] flex items-center bg-[#b69f7c] border-b shadow-lg">
        <div className="max-w-[1200px] w-full mx-auto flex justify-between items-center px-6">
          
          {/* 왼쪽 로고 */}
          <div className="flex items-center gap-4">
            <img src={image5} alt="logo" className="w-[50px] h-[50px]" />
            <h1 className="text-[#faf5e9] text-3xl font-bold">무드푸드</h1>
          </div>

          {/* 오른쪽 */}
          <div className="flex gap-6 items-center">
            <button className="text-[#faf5e9] text-lg hover:underline">서비스 흐름</button>
            <div className="text-[#faf5e9] text-lg font-semibold">{userName}님</div>

            {/* 프로필 이미지 (업데이트됨) */}
            <div
              className="w-12 h-12 rounded-full overflow-hidden border-2 border-[#faf5e9] cursor-pointer hover:scale-110 transition"
              onClick={handleProfile}
            >
              <img
                src={userImage}
                alt="profile"
                className="w-full h-full object-cover"
              />
            </div>
          </div>

        </div>
      </header>

      {/* 콘텐츠 */}
      <div className="flex justify-center mt-10 px-3">
        <div className="w-full max-w-[1100px] space-y-16">

          {/* ⭐ 지역 입력 */}
          <section className="bg-white border rounded-2xl shadow p-8">
            <h2 className="text-[#b69f7c] text-xl font-bold mb-4">어디서 식사하실 건가요?</h2>

            <input
              type="text"
              value={region}
              onChange={(e) => setRegion(e.target.value)}
              placeholder="예: 강남 / 홍대 / 수원"
              className="w-full border border-gray-300 rounded-md px-3 py-2"
            />
          </section>

          {/* ⭐ 기분 선택 */}
          <section className="bg-white border rounded-3xl shadow p-10">
            <h2 className="text-center text-[#a78c63] text-2xl font-bold mb-10">지금 기분이 어떠세요?</h2>

            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-4 gap-6 place-items-center">
              {moodOptions.map((mood) => (
                <button
                  key={mood.id}
                  onClick={() => setSelectedMood(mood.id)}
                  className={`
                    w-full max-w-[220px] h-28 flex flex-col justify-center items-center
                    ${mood.bgColor} ${mood.borderColor} border-2 rounded-xl
                    cursor-pointer hover:scale-105 transition
                    ${selectedMood === mood.id ? "ring-4 ring-offset-2 ring-[#b69f7c]" : ""}
                  `}
                >
                  <img src={mood.icon} className="w-10 h-10 mb-2" alt="" />
                  <span>{mood.label}</span>
                </button>
              ))}
            </div>
          </section>

          {/* ⭐ 음식 선택 */}
          <section className="bg-white border rounded-3xl shadow p-10">
            <h2 className="text-center text-[#a78c63] text-2xl font-bold mb-10">어떤 음식이 드시고 싶나요?</h2>

            <div className="grid grid-cols-2 md:grid-cols-5 gap-6 place-items-center">
              {foodOptions.map((food) => (
                <button
                  key={food.id}
                  onClick={() => setSelectedFood(food.id)}
                  className={`
                    w-full max-w-[200px] h-32 flex flex-col justify-center items-center
                    ${food.bgColor} ${food.borderColor} border-2 rounded-xl
                    cursor-pointer hover:scale-105 transition
                    ${selectedFood === food.id ? "ring-4 ring-offset-2 ring-[#b69f7c]" : ""}
                  `}
                >
                  <img src={food.image} className="w-12 h-12 mb-2" alt="" />
                  <span>{food.label}</span>
                </button>
              ))}
            </div>
          </section>

          {/* ⭐ 저장 버튼 */}
          <div className="flex justify-center mb-20">
            <button
              onClick={handleSaveAndNext}
              className="px-8 py-3 bg-[#b69f7c] text-white rounded-xl text-lg hover:bg-[#a18567] transition"
            >
              추천 메뉴 보러가기 →
            </button>
          </div>

        </div>
      </div>
    </div>
  );
};
