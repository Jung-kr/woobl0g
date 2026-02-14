## 📌 프로젝트 소개

**woobl0g**는 최근 급증하는 야구 팬층과 커뮤니티 수요를 타겟으로 한 MSA 기반 **배팅 기능을 포함한 야구 커뮤니티 서비스**입니다.

기존의 단순한 게시글 중심 야구 커뮤니티 서비스의 한계를 분석하여, KBO 경기 기반 배팅 시스템과 포인트 랭킹 시스템을 결합한 **게이미피케이션** 기반 커뮤니티 플랫폼으로 확장하여 구현하였습니다.

마이크로서비스 아키텍처(MSA), 이벤트 기반 비동기 처리(Kafka), Redis 기반 실시간 배당률 계산, 대규모 트래픽을 고려한 성능 개선 등 **운영 환경을 가정**한 아키텍처 설계와 구현에 중점을 두었습니다.

<br>

## 🏗️ 시스템 아키텍처

<img width="4296" height="2781" alt="woobl0g_architecture" src="https://github.com/user-attachments/assets/35f4582a-20c7-43ad-be66-16b7ba7b0fca" />

- AWS **VPC 기반** 네트워크 아키텍처로 Public / Private Subnet 분리 구성
- Bastion Host + Spring Cloud Gateway를 통한 **단일 진입점 및 운영 접근 통제**
- Private Subnet 내부 서비스는 **NAT Gateway**를 통한 **단방향 외부 통신**만 허용
- Kafka 기반 **비동기 이벤트 처리 구조**로 서비스 간 결합도 최소화 및 확장성 확보
- 마이크로서비스별 **독립 RDS 구성**으로 장애 전파 최소화 및 데이터 종속성 제거

<br>

## 🛠 기술 스택

| 구분        | 기술                                             |
| ----------- | ------------------------------------------------ |
| Language    | Java 17                                          |
| Framework   | Spring Boot 4.0.0, Spring Cloud, Spring Data JPA |
| Database    | MySQL                                            |
| Cache       | Redis                                            |
| Infra       | AWS, Docker, Apache Kafka                        |
| Test & Docs | JUnit5, Mockito, Swagger                         |

<br>

## 🚀 핵심 구현 내용

**1️⃣ Spring Cloud Gateway 기반 단일 진입점 & 공통 책임 중앙화**

- MSA 환경에서 분산된 엔드포인트를 Gateway 기반 단일 진입점 구조로 통합
- 인증·인가, 요청 라우팅, Swagger 문서 통합 등 공통 책임 중앙화  
  👉 **_클라이언트 코드 변경 제거 + 서비스 확장성 및 유지보수성 향상_**

**2️⃣ Gateway 중심 JWT 인증·인가 책임 분리**

- JWT 검증 로직을 Gateway 커스텀 필터로 통합하여 서비스별 중복 제거
- 인증과 인가를 분리한 2단계 필터 구조 설계  
  👉 **_보안 정책 일관성 확보 + 인증 로직 변경 시 수정 범위 최소화_**

**3️⃣ 이벤트 기반 데이터 동기화로 게시글 조회 성능 개선**

- 게시글 조회 시 발생하던 서비스 간 동기 호출 및 N+1 구조 제거
- Kafka 기반 사용자 정보 변경 이벤트 발행 → board-service 비동기 동기화 구조 설계
- 조회 전용 User 테이블 구성으로 게시글 조회 시 user-service 호출 완전 제거  
  👉 **_응답 시간 328ms → 118ms (64% 단축), 동기 호출 제거로 장애 전파 차단_**

**4️⃣ Kafka 비동기 포인트 적립 처리 & 장애 대응 설계**

- 포인트 적립 동기 구조 → Kafka 기반 비동기 이벤트 처리 구조 전환
- Retry + DLT + Slack 알림 기반 장애 감지·재처리 체계 구축  
  👉 **_처리량 120 TPS → 380 TPS (217% 향상), 장애 인지 시간 90% 단축_**

**5️⃣ 보상 트랜잭션 기반 서비스 간 일관성 보장**

- 포인트 차감 → 베팅 기록 저장 구조에서 발생하던 분산 트랜잭션 불일치 문제 해결
- 실패 발생 시 보상 트랜잭션(차감 → 재적립) 기반 최종적 일관성 확보  
   👉 **_MSA 환경에서도 서비스 간 원자성 보장 + 장애 복구 가능 구조 완성_**

<br>

## 🗄️ ERD

<img width="1400" height="767" alt="woobl0g_erd" src="https://github.com/user-attachments/assets/357aee61-8a63-4f11-9cab-6cc29c7c2043" />

본 프로젝트는 **MSA** 기반으로, 도메인별 책임을 명확히 분리하기 위해 서비스 단위로 **독립적인 DB를 분리**하여 설계하였습니다.

- 🟢 **user-service** : 회원 관리, 인증/인가, 사용자 기본 정보 관리  
- 🟠 **board-service** : 게시글, 댓글, 사용자 게시판 활동 관리  
- 🟡 **game-service** : 경기 정보 관리, 베팅 처리, 게임 상태 관리  
- 🔵 **point-service** : 포인트 적립/차감, 포인트 변경 이력, 실패 이력 관리  

<br>

## 📡 API 명세

**Swagger :** `http://localhost:8000/swagger-ui/index.html`

<details>
<summary><b>🔐 user-service</b></summary>

| Method | URL                        | Description      | Note                                                                                                |
| ------ | -------------------------- | ---------------- | --------------------------------------------------------------------------------------------------- |
| POST   | `/api/auth/signup`         | 회원가입         | Kafka 이벤트 발행 <br> → `point-service` 포인트 자동 적립 <br> → `board-service` 사용자 정보 동기화 |
| POST   | `/api/auth/login`          | 로그인           | Redis를 활용한 Refresh Token Rotation 전략                                                          |
| POST   | `/api/auth/refresh`        | 토큰 재발급      | -                                                                                                   |
| GET    | `/internal/users/{userId}` | 단일 사용자 조회 | -                                                                                                   |
| GET    | `/internal/users`          | 다중 사용자 조회 | -                                                                                                   |

</details>

<details>
<summary><b>📝 board-service</b></summary>

| Method | URL                                          | Description      | Note                                                      |
| ------ | -------------------------------------------- | ---------------- | --------------------------------------------------------- |
| GET    | `/api/boards`                                | 게시글 목록 조회 | -                                                         |
| POST   | `/api/boards`                                | 게시글 생성      | Kafka 이벤트 발행 <br> → `point-service` 포인트 자동 적립 |
| GET    | `/api/boards/{boardId}`                      | 게시글 단건 조회 | -                                                         |
| PATCH  | `/api/boards/{boardId}`                      | 게시글 수정      | -                                                         |
| DELETE | `/api/boards/{boardId}`                      | 게시글 삭제      | -                                                         |
| GET    | `/api/boards/{boardId}/comments`             | 댓글 목록 조회   | -                                                         |
| POST   | `/api/boards/{boardId}/comments`             | 댓글 생성        | Kafka 이벤트 발행 <br> → `point-service` 포인트 자동 적립 |
| PATCH  | `/api/boards/{boardId}/comments/{commentId}` | 댓글 수정        | -                                                         |
| DELETE | `/api/boards/{boardId}/comments/{commentId}` | 댓글 삭제        | 대댓글 존재 시 Soft Delete                                |

</details>

<details>
<summary><b>🎮 game-service</b></summary>

| Method | URL                                          | Description                 | Note                                                      |
| ------ | -------------------------------------------- | --------------------------- | --------------------------------------------------------- |
| GET    | `/api/games`                                 | 날짜별 경기 목록 조회       | -                                                         |
| GET    | `/api/games/{gameId}`                        | 경기 상세 조회              | Redis를 활용한 실시간 배당률 처리                         |
| POST   | `/api/bets/games/{gameId}`                   | 배팅하기                    | `point-service` 포인트 동기 차감                          |
| DELETE | `/api/bets/games/{gameId}`                   | 배팅 취소                   | Kafka 이벤트 발행 <br> → `point-service` 포인트 자동 적립 |
| GET    | `/api/bets`                                  | 배팅 내역 조회              | -                                                         |
| POST   | `/api/admin/kbo/crawling-jobs`               | KBO 일정 크롤링 (월별)      | -                                                         |
| POST   | `/api/admin/kbo/crawling-jobs/full`          | KBO 일정 크롤링 (전체 시즌) | -                                                         |
| POST   | `/api/admin/bets/games/{gameId}/settlements` | 배팅 정산                   | Kafka 이벤트 발행 <br> → `point-service` 포인트 자동 적립 |

</details>

<details>
<summary><b>💰 point-service</b></summary>
  
| Method | URL | Description | Note |
|--------|------|--------------|------|
| GET | `/api/points/ranking` | 포인트 랭킹 조회 | `user-service` 사용자 정보 배치 조회 |
| GET | `/api/points/history` | 내 포인트 이력 조회 | - |
| POST | `/internal/points/add` | 포인트 적립 | 재시도 실패시 Kafka 이벤트 발행 <br> → `point-service` 실패 내역 저장 <br> → `notification-service` slack 알림 |
| POST | `/internal/points/deduct` | 포인트 차감 | - |
| GET | `/api/admin/points/failures` | 포인트 적립 실패 내역 조회 | - |
| POST | `/api/admin/points/failures/{failureId}/retry` | 실패 적립 재시도 | - |
| POST | `/api/admin/points/failures/{failureId}/ignore` | 실패 적립 무시 처리 | - |
  
</details>

<br>

## 🚀 배포 및 실행 방법

**로컬 환경**

```bash
# 저장소 클론
$ git clone https://github.com/Jung-kr/woobl0g.git
$ cd woobl0g

# Docker 기반 인프라 실행 (MySQL, Redis, Kafka)
$ docker-compose -f docker-compose-local.yml up -d

# 각 마이크로서비스 실행
$ cd user-service && ./gradlew bootRun
$ cd ../board-service && ./gradlew bootRun
$ cd ../game-service && ./gradlew bootRun
$ cd ../point-service && ./gradlew bootRun
$ cd ../notification-service && ./gradlew bootRun
$ cd ../api-gateway-service && ./gradlew bootRun
```
