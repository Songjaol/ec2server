import json
from langchain_ollama import OllamaLLM
import re
import os
import time

llm = OllamaLLM(model="gemma3:12b")

CHUNK = 30  # 한 번 생성 수

TEMPLATE = """
한국인의 실제 음식 선호도, 감정 패턴, 지역별 분포를 반영하여
아래 JSON 배열 형태로 %d개의 음식 소비 로그를 생성해줘.

형식:
[
  {{
    "userId": 1~200,
    "mood": "행복|우울|스트레스|피곤|활기|로맨틱|편안|신남",
    "foodName": "한국 음식명",
    "foodType": "한식|양식|중식|일식|분식|패스트푸드|디저트",
    "region": "서울|경기|부산|대구|광주|대전|울산",
    "createdAt": "YYYY-MM-DD HH:mm:ss"
  }}
]

조건:
- 행복: 고기류, 치킨, 바비큐 비중 증가
- 우울: 국물요리, 라멘, 분식류 증가
- 스트레스: 매운 음식·중식 매운요리 비중 증가
- 피곤: 따뜻한 국물, 죽, 해장용 음식 증가
- 활기: 단백질 위주, 카페인 음료 증가
- 로맨틱: 일식, 파스타, 디저트 비중 증가
- 편안: 집밥류 증가
- 신남: 튀김류, 분식, 패스트푸드 증가
- JSON 배열만 출력
- createdAt은 **2025년 11월 기준 최근 3개월(90일) 안에서 랜덤**
- userid 1~200사이 범위를 꼭 지켜줘
"""

def generate_logs(count):
    prompt = TEMPLATE % count
    return llm.invoke(prompt)

def extract_pure_json(text: str) -> str:
    text = re.sub(r"```json", "", text)
    text = re.sub(r"```", "", text)
    match = re.search(r"\[.*\]", text, re.S)
    if not match:
        print("⚠ JSON 배열 추출 실패:", text[:200])
        raise ValueError("JSON 배열이 없음.")
    return match.group(0)

def load_existing_logs(path="food_logs.json"):
    if not os.path.exists(path):
        return []
    try:
        with open(path, "r", encoding="utf-8") as f:
            return json.load(f)
    except:
        return []

def save_logs(logs, path="food_logs.json"):
    with open(path, "w", encoding="utf-8") as f:
        json.dump(logs, f, ensure_ascii=False, indent=2)


if __name__ == "__main__":
    import sys
    total_count = int(sys.argv[1]) if len(sys.argv) > 1 else 5000

    loops = (total_count + CHUNK - 1) // CHUNK
    print(f"[Python] 총 {total_count}개 생성 → {CHUNK}개씩 {loops}번 요청")

    for i in range(loops):
        print(f"\n➡️ {i+1}/{loops} 번째 요청")

        # 기존 파일 다시 읽기 (중간에 파일이 바뀌어도 반영됨)
        existing = load_existing_logs()
        print(f"   📁 현재 파일 로그 수: {len(existing)}")

        raw = generate_logs(CHUNK)
        clean = extract_pure_json(raw)

        try:
            new_logs = json.loads(clean)
            print(f"   ✔ JSON 파싱 성공 → {len(new_logs)}개")
        except Exception as e:
            print("❌ JSON 파싱 오류:", clean[:200])
            raise e

        # 기존 + 신규 누적
        merged = existing + new_logs

        # ⭐ 루프마다 즉시 저장 ⭐
        save_logs(merged)

        print(f"   💾 저장됨 → 총 {len(merged)}개")

        time.sleep(1)

    print("\n🎉 모든 생성 완료!")
