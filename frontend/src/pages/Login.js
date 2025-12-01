import React, { useState } from "react";
import image2 from "../images/Love.png";
import image3 from "../images/Happy.png";
import image4 from "../images/Food.png";
import image5 from "../images/Relax.png";
import image6 from "../images/Excite.png";
import image from "../images/Dot.png";
import image1 from "../images/Login.svg";
import vector2 from "../images/EMail.svg";
import vector3 from "../images/Password.svg";
import vector4 from "../images/Google.svg";
import vector8 from "../images/Signin.svg";
import vector from "../images/Background.png";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axiosConfig"; // 'axios' 대신 'api' 사용

export const Login = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  // 데이터 구조는 그대로 유지
  const emotions = [
    { name: "행복", image: image3 },
    { name: "기쁨", image: image2 },
    { name: "편안함", image: image5 },
    { name: "설렘", image: image6 },
  ];

  const features = [
    { icon: image, text: "Al 기반 감정 분석" }, // background2, background 대신 image(Dot.png)로 통일
    { icon: image, text: "개인화된 맛집 추천" },
    { icon: image, text: "실시간 맛집 정보" },
  ];
// --- (수정 2) handleLogin이 event 객체 'e'를 받도록 수정 ---
const handleLogin = async (e) => {
  e.preventDefault();

  try {
    const response = await api.post("/api/user/login", {
      email,
      password,
    });

    console.log("🔥 서버 로그인 응답:", response.data);

    // 서버 응답에서 토큰 및 유저ID를 자동으로 추출
    const token =
      response.data.token ||
      response.data.accessToken ||
      response.data.jwt ||
      response.data.authToken ||
      null;

    const userId =
      response.data.userId ||
      response.data.id ||
      response.data.user?.id ||
      null;

    console.log("🔥 token:", token);
    console.log("🔥 userId:", userId);

    if (token && userId) {
      localStorage.setItem("token", token);
      localStorage.setItem("userId", userId);

      alert("로그인 성공!");
      navigate("/SelectFeel");
    } else {
      alert("⚠️ 서버 응답에 token 또는 userId가 없습니다.");
      console.error("응답 내용:", response.data);
    }

  } catch (error) {
    console.error("Login error:", error);
    alert("로그인 실패! 이메일 또는 비밀번호가 잘못되었습니다.");
  }
};



  const handleSignup = () => {
    navigate("/Register"); // 회원가입 페이지로 이동
  };

  // 핸들러 함수들은 그대로 유지
  // const handleLogin = (e) => e.preventDefault();
  const handleForgotPassword = (e) => e.preventDefault();
  const handleGoogleLogin = (e) => e.preventDefault();
  const handleKakaoLogin = (e) => e.preventDefault();
  // const handleSignup = (e) => e.preventDefault();

  return (
    // ✨ 1. 고정 픽셀 크기 대신 min-h-screen과 flex를 사용
    <div className="w-full min-h-screen flex flex-col lg:flex-row">
      {/* ✨ 2. 왼쪽 컨텐츠 영역 (모바일: 전체, 데스크탑: 3/5 너비)
        - flex-col: 컨텐츠를 세로로 쌓습니다.
        - justify-center: 세로 중앙 정렬
        - items-center: 가로 중앙 정렬 (텍스트 정렬은 text-center로)
        - p-12: 내부 여백
      */}
      <div className="w-full lg:w-3/5 bg-[#FBF4E4C4] p-12 flex flex-col justify-center items-center text-center relative overflow-hidden">
        {/* 로고: 절대 좌표 대신 컨테이너의 좌측 상단에 배치 */}
        <img
          className="absolute top-10 left-10 w-24 h-24 object-cover"
          alt="Logo"
          src={image4}
        />

        {/* ✨ 3. 절대 좌표 스타일(style prop)을 제거하고
             Tailwind 유틸리티 클래스(className)로 대체
        */}
        <h1 className="[font-family:'Inter-Bold',Helvetica] font-bold text-[#a78c62] text-6xl mb-8">
          무드 푸드
        </h1>

        <p className="[font-family:'Inter-Medium',Helvetica] font-medium text-[#a78c63] text-5xl leading-tight mb-6">
          당신의 감정을 읽고
          <br />
          완벽한 음식을 추천합니다
        </p>

        <p className="[font-family:'Inter-Regular',Helvetica] font-normal text-[#a78c63] text-3xl mb-4">
          행복할 때, 슬플 때, 외로울 때...
        </p>

        <p className="[font-family:'Inter-Regular',Helvetica] font-normal text-[#a78c63] text-3xl mb-16">
          모든 순간에 어울리는 맛집을 찾아드립니다.
        </p>

        {/* ✨ 4. emotions/features 영역도 Flexbox로 재작성
        */}
        <div className="flex justify-center gap-8 lg:gap-12 mb-12">
          {emotions.map((emotion) => (
            <div key={emotion.name} className="flex flex-col items-center gap-2">
              <img
                className="w-18 h-18 object-cover" // 크기 고정
                alt={emotion.name}
                src={emotion.image}
              />
              <div
                className="[font-family:'Inter-Regular',Helvetica] font-normal text-xl"
                style={{ color: emotion.style?.color || "#a78c63" }} // 색상은 유지
              >
                {emotion.name}
              </div>
            </div>
          ))}
        </div>

        <div className="flex flex-col items-start gap-4">
          {features.map((feature, index) => (
            <div key={index} className="flex items-center gap-4">
              <img
                className="w-5 h-5 object-cover" // 크기 고정
                alt="Feature icon"
                src={feature.icon}
              />
              <div className="[font-family:'Inter-Regular',Helvetica] font-normal text-[#a78c63] text-3xl">
                {feature.text}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* ✨ 5. 오른쪽 로그인 폼 영역 (모바일: 전체, 데스크탑: 2/5 너비)
        - bg-gray-100: 구분을 위한 다른 배경색
        - flex justify-center items-center: 폼을 화면 중앙에 배치
      */}
      <div className="w-full lg:w-2/5 bg-[#FBF4E4C4] flex justify-center items-center p-8">
        {/* 로그인 폼 컨테이너
          - ✨ 절대 좌표(style prop)를 제거.
          - w-full max-w-md: 너비를 100%로 하되, 최대 너비를 지정하여 너무 커지지 않게 함.
        */}
        <div className="w-full max-w-md flex flex-col">
          {/* 로그인 폼의 내부 코드(아이콘, 제목 등)도
            `absolute` 대신 `flex flex-col items-center`로 변경
          */}
          <div className="h-16 w-16 self-center flex items-center justify-center bg-[#b59f7e] rounded-full">
            <div className="h-16 w-16 relative">
              <img
                className="absolute w-[100.00%] h-[100.00%] top-[0%] left-[0%]"
                alt="Icon"
                src={vector}
              />
            </div>
          </div>

          <div className="h-10 self-center mt-4 text-[#b59f7e] text-4xl text-center [font-family:'Roboto-Bold',Helvetica] font-bold">
            무드푸드
          </div>

          <div className="h-6 self-center mt-2 [font-family:'Roboto-Regular',Helvetica] font-normal text-gray-600 text-base text-center">
            기분에 따라 맛집을 추천받으세요
          </div>

          {/* 로그인 폼 */}
          <form
            className="w-full relative mt-8 bg-[#b49f7f] rounded-lg border border-solid shadow-[0px_1px_2px_#0000000d] p-6"
            onSubmit={handleLogin}
          >
            {/* --- 헤더 --- */}
            <header className="w-full border-b pb-4 mb-6">
              <div className="flex items-center gap-2">
                <img className="w-6 h-6" alt="Login icon" src={image1} />
                <h2 className="text-[#FBF4E4] text-2xl [font-family:'Roboto-Bold',Helvetica] font-bold">
                로그인
                </h2>
              </div>
              <p className="mt-2 [font-family:'Roboto-Regular',Helvetica] font-normal text-gray-500 text-sm">
                계정에 로그인하여 맞춤형 맛집 추천을 받아보세요
              </p>
            </header>

            {/* --- 이메일 입력 --- */}
            <label
              htmlFor="email"
              className="block [font-family:'Roboto-Medium',Helvetica] font-medium text-black text-sm mb-2"
            >
              이메일
            </label>
            <div className="relative flex items-center w-full mb-4">
              <img
                className="absolute left-3 w-4 h-4"
                alt="Email icon"
                src={vector2}
              />
              <input
                id="email"
                className="w-full h-10 bg-white rounded-md border border-solid border-gray-300 pl-10 pr-3 [font-family:'Roboto-Regular',Helvetica] font-normal text-sm"
                placeholder="example@email.com"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                aria-label="이메일"
              />
            </div>

            {/* --- 비밀번호 입력 --- */}
            <label
              htmlFor="password"
              className="block [font-family:'Roboto-Medium',Helvetica] font-medium text-black text-sm mb-2"
              >
              비밀번호
            </label>
            <div className="relative flex items-center w-full">
              <img
                className="absolute left-3 w-4 h-4"
                alt="Password icon"
                src={vector3}
              />
              <input
                id="password"
                className="w-full h-10 bg-white rounded-md border border-solid border-gray-300 pl-10 pr-3 [font-family:'Roboto-Regular',Helvetica] font-normal text-sm"
                placeholder="••••••••"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                aria-label="비밀번호"
              />
            </div>

            {/* --- 비밀번호 찾기 --- */}
            <button
              type="button"
              onClick={handleForgotPassword}
              className="all-[unset] box-border w-full text-right mt-2 mb-4 cursor-pointer"
            >
              <div className="[font-family:'Roboto-Regular',Helvetica] font-normal text-orange-600 text-sm">
                비밀번호를 잊으셨나요?
              </div>
            </button>

          {/* --- 로그인 버튼 --- */}
<button
  type="submit"
  className="all-[unset] box-border flex items-center justify-center bg-[#fbf6ec] w-full h-10 rounded-md cursor-pointer [font-family:'Roboto-Medium',Helvetica] font-medium text-[#b59f7e] text-base"
>
  로그인
</button>

{/* 회원가입 안내 */}
<div className="[font-family:'Roboto-Regular',Helvetica] font-normal text-gray-600 text-sm text-center mt-6 mb-2">
  아직 계정이 없으신가요?
</div>

{/* --- 회원가입 버튼 --- */}
<button
  type="button"
  onClick={handleSignup}
  className="all-[unset] box-border bg-white border border-solid border-gray-300 w-full h-10 rounded-md cursor-pointer flex items-center justify-center gap-2"
>
  <img className="w-4 h-4" alt="Signup icon" src={vector8} />
  <div className="[font-family:'Roboto-Medium',Helvetica] font-medium text-black text-base">
    회원가입
  </div>
</button>

          </form>
          <footer className="self-center mt-6 [font-family:'Roboto-Regular',Helvetica] font-normal text-gray-500 text-sm">
            무드푸드 © 2025
          </footer>
        </div>
      </div>
    </div>
  );
};