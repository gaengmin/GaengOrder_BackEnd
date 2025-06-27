# 🧾 Gaeng'sOrder – QR 기반 테이블 오더 시스템

> 🚧 **진행 중인 백엔드 프로젝트** (최종 수정일: 2025.05.21 기준)  
QR 기반 테이블 주문 및 매장 운영을 위한 백엔드 API 시스템입니다.  
실제 매장에서 바로 사용 가능한 수준의 구조를 목표로, 도메인 주도 설계(DDD) 기반으로 구현하고 있습니다.  
인증, 주문 흐름, 매출 분석까지 모두 백엔드 중심으로 설계되었으며, CI/CD 및 성능 실험까지 확장 중입니다.

---

## 🎯 프로젝트 목표

- 실제 매장에서 바로 사용할 수 있는 수준의 테이블 주문 시스템 구현
- 확장성과 유지보수성을 고려한 백엔드 구조 설계
- 프론트와의 연동, 관리자 기능, Swagger API 문서화 등 협업 가능한 구조 지향
- ERD 리팩토링, 인증 설계, 성능 최적화 등 실전 문제 해결 기반의 성장 경험 축적

---

## 📘 문제 해결 중심 블로그 기록 (나혼프 시리즈)

> 단순 기능 구현이 아닌 **문제 정의 → 설계 고민 → 구조적 해결**의 흐름을 기록한 기술 블로그 시리즈입니다.

- 부분 취소 로직 설계 및 금액 정산 처리 → [Velog]([https://velog.io/@gaengmin/주문-상태-이력-테이블-리팩토링](https://velog.io/@gaengmin/%EB%82%98%ED%98%BC%ED%94%84%ED%85%8C%EC%9D%B4%EB%B8%94%EC%98%A4%EB%8D%94-%EC%B0%A8%EB%9D%BC%EB%A6%AC-%EC%B7%A8%EC%86%8C%ED%95%A0%EA%B1%B0%EB%A9%B4-%EC%A0%84%EC%B2%B4%EC%B7%A8%EC%86%8C%EB%A7%8C-%ED%95%98%EA%B3%A0-%EC%8B%B6%EB%8B%A4..-%EB%B6%80%EB%B6%84%EC%B7%A8%EC%86%8C....-%EA%B0%9C%EB%B0%9C%EA%B3%BCSpringMybatis))
- ERD 리팩토링을 통한 기능 개선 → [Velog]([https://velog.io/@gaengmin/도메인-주문-설계-리팩토링](https://velog.io/@gaengmin/%EB%82%98%ED%98%BC%ED%94%84ERD-%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81))
- DB낙관적 락을 통한 동시성 문제 해결 → [Velog]([https://velog.io/@gaengmin/부분취소-로직-구현](https://velog.io/@gaengmin/%ED%85%8C%EC%9D%B4%EB%B8%94%EC%98%A4%EB%8D%94%EC%8B%9C%EC%8A%A4%ED%85%9CDB%EA%B0%9C%EB%B0%9C-%EC%BD%94%EB%93%9C%EB%A6%AC%EB%B7%B0-%EC%A4%91-HTTP-Method-PUT%EA%B3%BC-DB-%EC%97%85%EB%8D%B0%EC%9D%B4%ED%8A%B8%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EB%AF%BC%EC%97%90-%EB%8C%80%ED%95%9C-%EB%B0%A9%EC%95%88-%EB%82%99%EA%B4%80%EC%A0%81-Lock%EC%9D%84-%EC%8B%A4%EC%A0%9C%EB%A1%9C-%EC%A0%81%EC%9A%A91))
-  복합인덱스를 통한 성능 최적화 → [Velog]([https://velog.io/@gaengmin/JWT-쿠키-인증-처리-흐름](https://velog.io/@gaengmin/%ED%85%8C%EC%9D%B4%EB%B8%94%EC%98%A4%EB%8D%94%EC%8B%9C%EC%8A%A4%ED%85%9CDB%EA%B0%9C%EB%B0%9C-%EC%BD%94%EB%93%9C%EB%A6%AC%EB%B7%B0-%EC%A4%91-DB-%EC%97%85%EB%8D%B0%EC%9D%B4%ED%8A%B8%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EB%AF%BC%EC%97%90-%EB%8C%80%ED%95%9C-%EB%B0%A9%EC%95%88-%EB%B3%B5%ED%95%A9%EC%9D%B8%EB%8D%B1%EC%8A%A4-%EC%A0%81%EC%9A%A9-%EC%84%B1%EB%8A%A5%EB%B9%84%EA%B5%902))
- 코드간결화를 위한 Refactoring → [Velog]([https://velog.io/@gaengmin/Enum과-전략패턴](https://velog.io/@gaengmin/%EB%82%98%ED%98%BC%ED%94%84%EC%96%B4%EB%96%BB%EA%B2%8C-%ED%95%98%EB%A9%B4-%EC%BD%94%EB%93%9C%EB%A5%BC-%EB%8D%94-%EA%B0%84%EA%B2%B0%ED%95%A0-%EC%88%98-%EC%9E%88%EC%9D%84%EA%B9%8C-Spring-JPA-%EC%BD%94%EB%93%9C%EB%B6%84%EB%A6%AC))

👉 [나혼프 - 개발 블로그 전체 보기](https://velog.io/@gaengmin/series/%EB%82%98%ED%98%BC%ED%94%84)

---

## 📌 프로젝트 개요

| 항목         | 내용 |
|--------------|------|
| 프로젝트명   | GaengOrder |
| 개발 기간     | 2025.05 ~ 진행 중 |
| 주요 기능     | QR 테이블 오더, 주문 관리, 관리자 매출 분석 |
| 기술 스택     | Spring Boot, MyBatis, JPA, MySQL, JWT, Swagger |
| 문서화        | Swagger 3, Velog |
| 배포 예정     | GitHub Actions + Docker + EC2, RDS |
| 저장소        | [GitHub Repository](https://github.com/gaengmin/GaengOrder_BackEnd) |
| 블로그        | [Velog 개발 블로그](https://velog.io/@gaengmin/posts?tag=%EB%82%98%ED%98%BC%ED%94%84) |
---
## 🎯 핵심 기능

### ✅ 고객 기능
- QR 코드 기반 비로그인 테이블 주문 
- 추가 주문 기능 
- 주문 상태 확인 및 변경
- JWT 기반 로그인/회원가입(매장 관리자 및 직원)

### ✅ 관리자/직원 기능
- 매장 정보/테이블/메뉴 관리
- 주문 상태 변경 처리 
- 매출/판매 분석 API 
  - 일별|주간|월간 매출

### 🚧 개발 중 기능 (예정)
- CI/CD 자동 배포 (GitHub Actions → EC2)
- 관리자 대시보드 프론트 (추후 Vue 또는 React)
- 알림 기능 (WebSocket or SSE)

---

## 📐 ERD 설계

> 실시간 주문/추가 주문/상태 이력/가격 이력까지 반영된 실무형 ERD

<img width="786" alt="image" src="https://github.com/user-attachments/assets/26b5f41d-2599-45e0-907b-b61426a83a77" />


- `orders` + `orders_items`: 기본 주문 구조
- `orders_status_log`: 주문 상태 변경 이력
- `is_added`, `cancel_reason`, `is_free` 등 주문 세부 구분 컬럼
- 가격/이름 이력은 주문 시점 `orders_items`에 복사 저장
- Soft Delete는 메뉴, 유저, 테이블 등에만 적용

---

## 🔒 인증 처리

- JWT 기반 Access/Refresh 토큰 발급
- Spring Security + Filter 기반 인증 흐름
- `auth/filter` 내부: `LoginFilter`, `JWTFilter`, `LogoutFilter`
- 쿠키를 통한 Refresh Token 관리
- 사용자 권한(Role)에 따른 분기 처리

---

## 📦 패키지 구조

DDD 설계 기반으로 각 도메인을 중심으로 분리 구성

```
src/main/
├── java/
│   └── tableOrder/
│       ├── analytics              # 통계 및 분석 관련 기능
│       ├── auth                   # 인증 및 인가 관련 기능 (JWT 등)
│       ├── category               # 메뉴 카테고리 관련 기능
│       ├── common                 # 공통 DTO, 예외, 유틸리티
│       ├── config                 # 전역 설정 (Security, Swagger 등)
│       ├── menu                   # 메뉴 관련 기능
│       ├── orders                 # 주문 관련 기능
│       ├── ordersItem            # 주문 상세 관련 기능
│       ├── ordersStatusLog       # 주문 상태 변경 로그 기록
│       ├── refresh                # 리프레시 토큰 관련 엔티티/레포지토리
│       ├── stores                 # 매장(가맹점) 관련 기능
│       ├── tables                 # 테이블 관련 기능
│       └── users                  # 사용자 관련 기능
└── resources/
    ├── db                        # DB 초기화 및 마이그레이션 스크립트
    └── mapper                   # MyBatis 또는 XML 기반 매퍼 파일
````

- DTO는 `request`, `response`로 명확히 분리
- Mapper + JPA 병행 적용 (유연한 DAO 계층 구성)
- Exception은 `GlobalExceptionHandler`를 통해 통합 처리

---

## 🛠 사용 기술 상세

| 항목 | 내용 |
|------|------|
| 프레임워크 | Spring Boot 3 |
| ORM | MyBatis 및 JPA(일부) |
| DB | MySQL 8 |
| 보안 | JWT (Access/Refresh), Security FilterChain |
| 문서화 | Swagger (springdoc-openapi) |
| 배포 | (예정) Docker + GitHub Actions + EC2 |
| 형상 관리 | Git, GitHub |

## 🗓️ 개발 일정

| 기간                  | 내용                               |
|---------------------|----------------------------------|
| 2025.05.03 \~ 05.06 | 도메인 설계, ERD, 테이블 구조 설계           |
| 2025.05.06 \~ 05.20 | 회원가입, 메뉴/테이블/매장 관리 API 개발        |
| 2025.05.21 \~ 05.25 | 주문 기능 구현 중 (주문 처리)               |
| 2025.05.26 \~ 05.31 | 관리자 매출 분석, 인기메뉴 조회 API 완성 예정     |
| 2025.06 이후          | CI/CD, 인덱싱 실험, 성능 최적화, 관리자 프론트 연동 |


## 📈 TO DO LIST

* V 주문 API 완료 및 Swagger 시연 가능 상태 만들기
* V 관리자 매출/판매 분석 API 고도화
* [ ] GitHub Actions + Docker + EC2 기반 CI/CD 자동화
* V 인덱싱 성능 비교 실험 및 블로그 정리
* [ ] 알림 기능 (WebSocket) 및 관리자용 프론트 분리
---



