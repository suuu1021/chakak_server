# CHAKAK Server 📸

사진작가 매칭 플랫폼 백엔드 API 서버

## 📋 프로젝트 소개

CHAKAK의 Spring Boot 기반 REST API 서버입니다. 사용자 인증, 예약 관리, 결제 처리, 실시간 채팅 등 모바일 앱의 모든 백엔드 기능을 제공합니다.

## 🛠 기술 스택

### Core
- **Java** 21
- **Spring Boot** 3.5.5
- **Spring Data JPA**
- **Gradle** 빌드 시스템

### Database
- **H2 Database** (개발/테스트)
- **MySQL** (프로덕션 지원)

### Authentication & Security
- **JWT** (java-jwt 4.4.0) - 토큰 기반 인증

### Payment
- **Kakao Pay API** - 결제 처리

### Communication
- **WebSocket** (STOMP) - 실시간 채팅
- **Spring WebFlux** - 비동기 HTTP 클라이언트
- **Spring Mail** - 이메일 인증

### Utilities
- **Lombok** - 보일러플레이트 코드 제거
- **Spring Validation** - 입력값 검증
- **Spring Dotenv** - 환경 변수 관리

## 📁 프로젝트 구조

```
src/main/java/com/green/chakak/chakak/
├── _global/                        # 전역 설정 및 유틸리티
│   ├── config/                     # Spring 설정
│   │   ├── AppConfig.java
│   │   ├── WebMvcConfig.java
│   │   └── WebSocketConfig.java
│   ├── errors/                     # 예외 처리
│   │   ├── MyExceptionHandler.java
│   │   └── exception/              # 커스텀 예외 (400, 401, 403, 404, 500)
│   ├── interceptor/                # 인터셉터
│   │   └── LoginInterceptor.java
│   ├── argument_resolver/          # ArgumentResolver
│   │   └── LoginUserArgumentResolver.java
│   └── utils/                      # 유틸리티
│       ├── ApiUtil.java
│       ├── FileUploadUtil.java
│       ├── HashUtil.java
│       ├── JwtUtil.java
│       └── PageUtil.java
│
├── account/                        # 회원 관리
│   ├── controller/
│   │   ├── UserRestController.java
│   │   ├── UserProfileRestController.java
│   │   └── UserTypeController.java
│   ├── domain/
│   │   ├── User.java
│   │   ├── UserProfile.java
│   │   ├── UserType.java
│   │   └── LoginUser.java
│   ├── service/
│   │   ├── UserService.java
│   │   ├── UserProfileService.java
│   │   ├── external/               # 외부 API 연동
│   │   ├── repository/             # JPA Repository
│   │   ├── request/                # 요청 DTO
│   │   └── response/               # 응답 DTO
│
├── admin/                          # 관리자 기능
│   ├── controller/
│   ├── domain/
│   ├── service/
│
├── banner/                         # 배너 관리
│   ├── controller/
│   ├── domain/
│   ├── service/
│
├── booking/                        # 예약 관리
│   ├── controller/
│   │   ├── BookingInfoRestController.java
│   │   └── BookingCancelInfoRestController.java
│   ├── domain/
│   │   ├── BookingInfo.java
│   │   ├── BookingCancelInfo.java
│   │   └── BookingStatus.java
│   ├── service/
│
├── chat/                           # 실시간 채팅
│   ├── controller/
│   │   ├── ChatController.java           # WebSocket 핸들러
│   │   └── ChatRoomController.java       # REST API
│   ├── domain/
│   │   ├── ChatMessage.java
│   │   └── ChatRoom.java
│   ├── repository/
│   ├── service/
│
├── community/                      # 커뮤니티
│   ├── controller/
│   │   ├── PostController.java
│   │   ├── ReplyController.java
│   │   └── LikeController.java
│   ├── domain/
│   │   ├── Post.java
│   │   ├── Reply.java
│   │   └── Like.java
│   ├── service/
│
├── email_verification/             # 이메일 인증
│   ├── EmailController.java
│   ├── EmailService.java
│   ├── EmailVerification.java
│   └── EmailVerificationRepository.java
│
├── payment/                        # 결제 시스템
│   ├── controller/
│   │   └── PaymentRestController.java
│   ├── domain/
│   │   ├── Payment.java
│   │   └── PaymentStatus.java
│   ├── repository/
│   │   ├── request/
│   │   └── response/               # Kakao Pay 응답 포함
│   ├── service/
│
├── photo/                          # 포토 서비스
│   ├── controller/
│   │   ├── PhotoRestController.java
│   │   ├── PhotoCategoryRestController.java
│   │   ├── PhotoMappingRestController.java
│   │   └── PhotoServiceReviewController.java
│   ├── domain/
│   │   ├── PhotoServiceInfo.java
│   │   ├── PhotoServiceCategory.java
│   │   ├── PhotoServiceMapping.java
│   │   ├── PhotoServiceReview.java
│   │   └── PriceInfo.java
│   ├── service/
│
├── photographer/                   # 사진작가 프로필
│   ├── controller/
│   │   ├── PhotographerController.java
│   │   └── PhotographerCategoryController.java
│   ├── domain/
│   │   ├── PhotographerProfile.java
│   │   ├── PhotographerCategory.java
│   │   └── PhotographerMap.java
│   ├── service/
│
└── portfolios/                     # 포트폴리오
    ├── controller/
    │   ├── PortfolioController.java
    │   └── PortfolioCategoryController.java
    ├── domain/
    │   ├── Portfolio.java
    │   ├── PortfolioCategory.java
    │   ├── PortfolioImage.java
    │   └── PortfolioMap.java
    ├── service/
```

## 🎯 주요 기능

### 1. 인증 & 회원 관리
- ✅ JWT 기반 토큰 인증
- ✅ 이메일 인증
- ✅ 사용자/사진작가 타입 관리
- ✅ 프로필 관리 (CRUD)

### 2. 예약 시스템
- ✅ 예약 생성/조회/수정/취소
- ✅ 예약 상태 관리 (대기/승인/완료/취소)
- ✅ 예약 취소 이력 관리

### 3. 결제
- ✅ 카카오페이 연동
- ✅ 결제 준비/승인/실패 처리
- ✅ 결제 내역 조회

### 4. 실시간 채팅
- ✅ WebSocket (STOMP) 기반
- ✅ 1:1 채팅방 생성
- ✅ 텍스트/이미지 메시지
- ✅ 채팅 내역 저장 및 조회

### 5. 포토 서비스
- ✅ 서비스 등록/조회/수정/삭제
- ✅ 카테고리별 분류
- ✅ 가격 옵션 관리
- ✅ 서비스 리뷰 시스템

### 6. 포트폴리오
- ✅ 포트폴리오 CRUD
- ✅ 이미지 다중 업로드
- ✅ 카테고리 태깅
- ✅ 사진작가별 조회

### 7. 커뮤니티
- ✅ 게시글 작성/조회/수정/삭제
- ✅ 댓글 시스템
- ✅ 좋아요 기능
- ✅ 페이징 처리

### 8. 관리자
- ✅ 배너 관리
- ✅ 관리자 기능 (회원관련 기능 관리)

### 99. 기타
- ✅ 파일 업로드 (Base64/Multipart)
- ✅ 전역 예외 처리

## 🚀 시작하기

### 사전 요구사항

- **JDK** 21 이상
- **Gradle** 8.x 이상
- **MySQL** 8.x (프로덕션용, 선택사항)

### 환경 변수 설정

프로젝트 루트에 `.env` 파일 생성:

```env
# 카카오페이
KAKAO_PAY_SECRET_KEY=your_kakao_pay_secret_key
KAKAO_PAY_CID=TC0ONETIME
KAKAO_PAY_API_URL=https://open-api.kakaopay.com

# 서버 도메인
SERVER_DOMAIN=http://localhost:8080

# Gmail SMTP (이메일 인증용)
spring.mail.username=your_gmail@gmail.com
spring.mail.password=your_app_password
```

### 설치 및 실행

1. **저장소 클론**
```bash
git clone https://github.com/your-username/chakak-server.git
cd chakak-server
```

2. **빌드**
```bash
./gradlew clean build
```

3. **실행**
```bash
# 개발 모드 (H2 DB)
./gradlew bootRun

# 또는 JAR 실행
java -jar build/libs/chakak-0.0.1-SNAPSHOT.jar
```

4. **H2 콘솔 접속** (개발용)
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: chakak
Password: chakak1234
```

## 🔧 프로필 설정

### application.yml 프로필

- **default**: 기본 설정
- **local**: 로컬 개발 환경
- **dev**: 개발 서버
- **secret**: 민감 정보 (Git 제외)

프로필 지정 실행:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 📡 API 엔드포인트 예시

### 인증
```
POST   /api/auth/login              # 일반 로그인
POST   /api/auth/signup             # 회원가입
POST   /api/auth/logout             # 로그아웃
```

### 사용자
```
GET    /api/users/{id}              # 사용자 조회
PUT    /api/users/{id}              # 사용자 수정
GET    /api/users/{id}/profile      # 프로필 조회
PUT    /api/users/{id}/profile      # 프로필 수정
```

### 예약
```
POST   /api/bookings                # 예약 생성
GET    /api/bookings                # 예약 목록
GET    /api/bookings/{id}           # 예약 상세
PUT    /api/bookings/{id}           # 예약 수정
DELETE /api/bookings/{id}           # 예약 취소
```

### 채팅
```
GET    /api/chat/rooms              # 채팅방 목록
POST   /api/chat/rooms              # 채팅방 생성
GET    /api/chat/rooms/{id}         # 채팅 내역

# WebSocket
CONNECT /ws                         # WebSocket 연결
SEND    /app/chat.send              # 메시지 전송
SUBSCRIBE /topic/chat/{roomId}     # 채팅방 구독
```

### 결제
```
POST   /api/payments/ready          # 결제 준비
POST   /api/payments/approve        # 결제 승인
GET    /api/payments                # 결제 내역
```

## 🏗 아키텍처 패턴

### 레이어 구조

```
Controller (REST API)
    ↓
Service (Business Logic)
    ↓
Repository (Data Access)
    ↓
Domain (Entity)
```

### 주요 설계 원칙

- **계층형 아키텍처**: Controller - Service - Repository 분리
- **DTO 패턴**: Request/Response DTO로 데이터 전송
- **전역 예외 처리**: `@RestControllerAdvice`로 일관된 에러 응답
- **JWT 인증**: Stateless 인증 방식
- **ArgumentResolver**: `@LoginUser` 어노테이션으로 인증 정보 주입

## 🔐 보안

- **JWT 토큰 기반 인증**: Stateless 세션 관리
- **LoginInterceptor**: API 엔드포인트 인증 검증
- **환경 변수 관리**: 민감 정보 .env 파일 분리
- **CORS 설정**: WebMvcConfig에서 관리

## 📝 코딩 컨벤션

### 패키지 구조
- `controller`: REST API 엔드포인트
- `service`: 비즈니스 로직
- `repository`: 데이터 액세스 (JpaRepository)
- `domain`: JPA 엔티티
- `service.request`: 요청 DTO
- `service.response`: 응답 DTO

### 네이밍 규칙
- **Controller**: `*RestController`, `*Controller`
- **Service**: `*Service`
- **Repository**: `*JpaRepository`, `*Repository`
- **DTO**: `*Request`, `*Response`, `*Dto`
- **Entity**: 도메인 이름 그대로 (예: `User`, `Booking`)

### 예외 처리
- `Exception400`: Bad Request
- `Exception401`: Unauthorized
- `Exception403`: Forbidden
- `Exception404`: Not Found
- `Exception500`: Internal Server Error

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests PortfolioCategoryJpaRepositoryTest
```

## 📦 빌드 및 배포

### JAR 빌드
```bash
./gradlew clean build
```

## 👥 팀

- **Backend Developer**: [Your Name]
