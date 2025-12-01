import React, { useState } from "react";
import image2 from "../images/Home.png";
import image from "../images/Profile.png";
import { Link, useNavigate } from "react-router-dom";
import api from '../api/axiosConfig'; // 2. 이전에 만든 axios 인스턴스(api.js) 임포트

export const Register = () => {
  const navigate = useNavigate();

  // --- 3. (신규) 이메일, 비밀번호 state 추가 ---
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  // ---

  const [name, setName] = useState("");
  const [preferredArea, setPreferredArea] = useState("");
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [selectedPriceRange, setSelectedPriceRange] = useState("");

  // ✨ 레이블을 명확하게 수정
  const foodType = [
    { id: "korean", label: "한식", icon: null },
    { id: "chinese", label: "중식", icon: null },
    { id: "japanese", label: "일식", icon: null },
    { id: "western", label: "양식", icon: null },
    { id: "category5", label: "아시안", icon: null }, // 레이블 수정
    { id: "category6", label: "기타", icon: null }, // 레이블 수정
  ];

  const priceRanges = [
    { id: "cheap", label: "저렴 (1만원 이하)" },
    { id: "moderate", label: "보통 (2만원이상 5만원 이하)" },
    { id: "expensive", label: "비쌈(5만원 이상)" },
  ];

  const toggleCategory = (categoryId) => {
    setSelectedCategories((prev) =>
      prev.includes(categoryId)
        ? prev.filter((id) => id !== categoryId)
        : [...prev, categoryId]
    );
  };


  // --- 5. (수정) handleSubmit: 회원가입 로직으로 변경 ---
  const handleSubmit = async () => {
    // 6. 모든 필드 데이터 수집
    const registrationData = {
      email,
      password,
      name,
      preferredArea,
      selectedCategories, // 배열
      selectedPriceRange, // 문자열
    };

    try {
        // 7. '/api/user/register' 엔드포인트로 전송
        const response = await api.post("/api/user/register", registrationData);

        // 8. 서버로부터 받은 토큰 추출
        const { token } = response.data;

        if (token) {
            // 9. 토큰을 localStorage에 저장 (자동 로그인)
            localStorage.setItem("token", token);
            alert("회원가입에 성공했습니다!");
            navigate('/'); // 로그인 페이지로 돌아감
        }

    } catch (error) {
        console.error("Registration error:", error);
        if (error.response && error.response.status === 400) {
            // 400 Bad Request (예: 이메일 중복)
            alert(error.response.data); // "이미 가입된 이메일입니다."
        } else {
            alert("회원가입 중 오류가 발생했습니다.");
        }
    }
  };
  // --- (handleSubmit 수정 끝) ---

  return (
    // ✨ 1. 절대 좌표/고정 크기 대신 flex-col과 min-h-screen 사용
    <div className="bg-[#fbf4e4c4] overflow-hidden w-full min-h-screen flex flex-col items-center py-12 px-4">
      
      {/* ✨ 2. 헤더: absolute 제거, text-center와 margin(mb)으로 변경 */}
      <header className="text-center mb-10">
        <h1 className="[font-family:'Inter-SemiBold',Helvetica] font-semibold text-[#a78c62] text-4xl md:text-6xl tracking-tight">
          프로필 정보 설정
        </h1>
        <p className="[font-family:'Inter-SemiBold',Helvetica] font-semibold text-[#a78c63] text-2xl md:text-3xl mt-2">
          당신의 취향을 알려주세요
        </p>
      </header>

      {/* ✨ 3. 메인 폼: absolute 제거, max-w-6xl로 너비 제한, padding(p-8) 추가 */}
      <main className="w-full max-w-7xl bg-[#a78c63cf] rounded-[60px] p-8 md:p-16 flex flex-col items-center">
        
        {/* ✨ 4. 프로필 사진: absolute 제거, flex-col items-center로 중앙 정렬 */}
        <section aria-label="프로필 사진" className="flex flex-col items-center mb-12">
          <img
            className="w-40 h-40 md:w-56 md:h-56 object-cover rounded-full" // ✨ 원형으로 변경
            alt="프로필 사진"
            src={image}
          />
          <button
            type="button"
            className="mt-4 w-40 h-14 bg-[#fffeff] rounded-[5.5px] border border-solid border-[#dbbb97] flex items-center justify-center gap-2 cursor-pointer hover:bg-[#f5f0e4] transition-colors"
            aria-label="프로필 사진 변경"
          >
            <img
              className="w-[29px] h-[25px] object-cover"
              alt=""
              src={image2}
            />
            <span className="[font-family:'Inter-Light',Helvetica] font-light text-[#cdb9a3] text-xl">
              사진 변경
            </span>
          </button>
        </section>

        {/* ✨ 5. 폼 그리드: 2단 그리드로 변경 (md 스크린 이상) */}
        <div className="w-full grid grid-cols-1 md:grid-cols-2 gap-12">
          
          {/* --- 왼쪽 열 --- */}
          <div className="flex flex-col gap-8">
            {/* --- 6. (신규) 이메일 필드 추가 --- */}
            <section aria-label="이메일 입력">
              <label
                htmlFor="email-input"
                className="block mb-3 [font-family:'Inter-Regular',Helvetica] font-normal text-[#fffbf2] text-2xl"
              >
                이메일 (로그인 ID)
              </label>
              <div className="w-full h-[57px] bg-[#faf5e9] rounded-[28.5px]">
                <input
                  id="email-input"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="이메일을 입력하세요."
                  className="w-full h-full px-6 bg-transparent [font-family:'Inter-Regular',Helvetica] font-normal text-[#a28c6b] text-2xl tracking-[0] leading-[normal] focus:outline-none focus:ring-2 focus:ring-[#a78c63] rounded-[28.5px]"
                  aria-required="true"
                />
              </div>
            </section>
            {/* --- 7. (신규) 비밀번호 필드 추가 --- */}
            <section aria-label="비밀번호 입력">
              <label
                htmlFor="password-input"
                className="block mb-3 [font-family:'Inter-Regular',Helvetica] font-normal text-[#fffbf2] text-2xl"
              >
                비밀번호
              </label>
              <div className="w-full h-[57px] bg-[#faf5e9] rounded-[28.5px]">
                <input
                  id="password-input"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="비밀번호를 입력하세요."
                  className="w-full h-full px-6 bg-transparent [font-family:'Inter-Regular',Helvetica] font-normal text-[#a28c6b] text-2xl tracking-[0] leading-[normal] focus:outline-none focus:ring-2 focus:ring-[#a78c63] rounded-[28.5px]"
                  aria-required="true"
                />
              </div>
            </section>
            <section aria-label="이름 입력">
              <label
                htmlFor="name-input"
                className="block mb-3 [font-family:'Inter-Regular',Helvetica] font-normal text-[#fffbf2] text-2xl"
              >
                이름
              </label>
              <div className="w-full h-[57px] bg-[#faf5e9] rounded-[28.5px]">
                <input
                  id="name-input"
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="이름을 입력하세요."
                  className="w-full h-full px-6 bg-transparent [font-family:'Inter-Regular',Helvetica] font-normal text-[#a28c6b] text-2xl tracking-[0] leading-[normal] focus:outline-none focus:ring-2 focus:ring-[#a78c63] rounded-[28.5px]"
                  aria-required="true"
                />
              </div>
            </section>

            <section aria-label="선호지역 입력">
              <label
                htmlFor="area-input"
                className="block mb-3 [font-family:'Inter-Regular',Helvetica] font-normal text-[#fffbf2] text-2xl"
              >
                선호지역
              </label>
              <div className="w-full h-[57px] bg-[#faf5e9] rounded-[28.5px]">
                <input
                  id="area-input"
                  type="text"
                  value={preferredArea}
                  onChange={(e) => setPreferredArea(e.target.value)}
                  placeholder="예: 강남구, 홍대, 이태원"
                  className="w-full h-full px-6 bg-transparent [font-family:'Inter-Regular',Helvetica] font-normal text-[#a28c6b] text-2xl tracking-[0] leading-[normal] focus:outline-none focus:ring-2 focus:ring-[#a78c63] rounded-[28.5px]"
                />
              </div>
            </section>

            <section aria-label="음식 카테고리 선택">
              <p className="mb-3 [font-family:'Inter-Regular',Helvetica] font-normal text-[#fffbf2] text-2xl tracking-[0] leading-[normal]">
                선호하는 음식 카테고리* (중복 선택 가능)
              </p>
              {/* ✨ 6. 버그 수정: 중복 map 제거, absolute 대신 flex-wrap으로 자동 줄바꿈 */}
              <div className="flex flex-wrap gap-2">
                {foodType.map((category) => {
                  const isSelected = selectedCategories.includes(category.id);
                  return (
                    <button
                      key={category.id}
                      type="button"
                      onClick={() => toggleCategory(category.id)}
                      className={`min-w-[120px] h-[40px] px-3 rounded-[5.5px] border border-solid transition-colors cursor-pointer ${
                        isSelected
                          ? "bg-[#F8F2E5] border-[#ddbf99]"
                          : "bg-[#80A5B8] border-[#ffffff]"
                      } hover:bg-[#f5f0e4]`}
                      aria-pressed={isSelected}
                      aria-label={`${category.label} ${
                        isSelected ? "선택됨" : "선택 안됨"
                      }`}
                    >
                      <span
                        className={`[font-family:'Inter-Regular',Helvetica] font-normal text-[20px] text-[#a78c63]`}
                      >
                        {category.label}
                      </span>
                    </button>
                  );
                })}
              </div>
              {/* ✨ 중복되었던 두 번째 map 제거 */}
            </section>
          </div>

          {/* --- 오른쪽 열 --- */}
          <div className="flex flex-col">
            <section aria-label="선호가격대 선택">
              <p className="mb-3 [font-family:'Inter-Regular',Helvetica] font-normal text-[#fffbf2] text-2xl whitespace-nowrap">
                선호가격대
              </p>
              {/* ✨ 7. 가격대: absolute 제거, fieldset과 flex-col로 변경 */}
              <fieldset className="w-full max-w-md">
                <legend className="sr-only">선호가격대 선택</legend>
                <div className="flex flex-col gap-4"> {/* ✨ 수직 정렬 및 간격 */}
                  {priceRanges.map((range) => {
                    const isSelected = selectedPriceRange === range.id;
                    return (
                      <div key={range.id}> {/* ✨ absolute 제거 */}
                        <label
                          htmlFor={`price-${range.id}`}
                          className="cursor-pointer"
                        >
                          <input
                            id={`price-${range.id}`}
                            type="radio"
                            name="priceRange"
                            value={range.id}
                            checked={isSelected}
                            onChange={(e) =>
                              setSelectedPriceRange(e.target.value)
                            }
                            className="sr-only" // ✨ 화면에서 숨김
                          />
                          <div
                            className={`w-full h-11 rounded-[28.5px] flex items-center justify-center transition-colors ${
                              isSelected ? "bg-[#e8dcc8]" : "bg-[#faf5e9]"
                            } hover:bg-[#e8dcc8]`}
                          >
                            <span className="[font-family:'Inter-Regular',Helvetica] font-normal text-[#b69f7c] text-base">
                              {range.label}
                            </span>
                          </div>
                        </label>
                      </div>
                    );
                  })}
                </div>
              </fieldset>
            </section>
          </div>
        </div>
        {/* --- 그리드 끝 --- */}

        {/* ✨ 8. 제출 버튼: absolute 제거, mt-16과 self-center로 배치 */}
        <button
          type="button"
          onClick={handleSubmit}
          className="mt-16 w-full max-w-2xl h-[102px] bg-[#f8f2e5] rounded-[28.5px] cursor-pointer hover:bg-[#ede3d1] transition-colors focus:outline-none focus:ring-4 focus:ring-[#a78c63] flex items-center justify-center" // ✨ flex로 내부 텍스트 중앙 정렬
          aria-label="프로필 저장하고 시작하기"
        >
          {/* ✨ span에서 absolute 제거 */}
          <span className="[font-family:'Inter-Bold',Helvetica] font-bold text-[#a78c63] text-[32px] tracking-[0] leading-[normal]">
            프로필 저장하고 시작하기
          </span>
        </button>
      </main>

      {/* ✨ 9. 푸터: absolute 제거, mt-8로 간격 조절 */}
      <footer className="text-center mt-8 pb-8">
        <p className="[font-family:'Inter-Regular',Helvetica] font-normal text-[#ada8a0] text-sm">
          © 2025 Mood Food
        </p>
      </footer>
    </div>
  );
};