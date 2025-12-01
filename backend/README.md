
1단계 먼저 제가 올린 코드 모두를 clone해서 모든 파일을 받습니다.

2단계 dockerfile과 같은 위치에 .env 파일을 만듭니다
예를들어
```bash
project/
├─ docker-compose.yml           # 도커 서비스 정의 (MySQL + Spring Boot)
├─ .env                         # 🔒 실제 환경 변수 (API 키, DB 비밀번호 등) — 공유 금지
├─ .gitignore                   # Git에 올리지 않을 파일 목록
├─ Dockerfile                   # Spring Boot 서버 이미지 빌드 설정
└─ src/                         # 백엔드 (Spring Boot) 소스 코드
```
위와 같이 추가하면 됩니다. 제가 알려드린 코드를 복사붙혀 넣기합니다.
그러고 터미널창에서 다운받은 폴더가 있는 곳에가서
docker-compose up -d 명령어를 실행합니다
그러면 서버와 데이터베이스가 실행될 것입니다. 
서버 포트는 8081입니다.
도커 mysql 실행해서 데이터 확인하는법

```bash
# 1. 컨테이너 접속
docker exec -it restaurant-mysql mysql -u root -p
# 비밀번호 입력 (MYSQL_ROOT_PASSWORD) 1234

# 2. 데이터베이스 선택
USE restaurant_db;

# 3. 테이블 데이터 보기
SELECT * FROM restaurant;

# 4. 테이블 데이터 초기화
TRUNCATE TABLE restaurant;
```
수동 pull request
https://github.com/Opensource-Teamproject10/Opensource-Project-Backend/compare/feature/server...Songjaol:feature/server

테스트 url
1. 지역+음식
   http://localhost:8081/api/recommend/restaurants?region=%ED%99%8D%EB%8C%80&food=%EC%B9%98%ED%82%A8

2. 음식+기분
   http://localhost:8081/api/recommend/foods?mood=%ED%96%89%EB%B3%B5&foodType=%ED%95%9C%EC%8B%9D
