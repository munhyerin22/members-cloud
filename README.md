# 🌩️ Project Cloud - 팀원 정보 관리 API

Spring Boot 기반의 팀원 정보 저장/조회 및 프로필 사진 업로드 API 서버입니다.  
AWS 인프라(EC2, RDS, S3, Parameter Store)를 활용하여 Stateless 아키텍처로 배포되었습니다.

---

## 📌 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.3 |
| DB (Local) | H2 (In-Memory) |
| DB (Prod) | MySQL (AWS RDS) |
| Cloud | AWS EC2, RDS, S3, SSM Parameter Store |
| Build | Gradle |

---

## 🏗️ 아키텍처

```
[Client]
   │
   ▼
[EC2 - Public Subnet]  ──→  [S3 Bucket] (프로필 이미지)
   │
   ▼
[RDS MySQL - Public Subnet]
   (보안그룹: EC2 SG만 허용)

AWS SSM Parameter Store → DB 접속 정보 주입
IAM Role → EC2에 S3/SSM 접근 권한 부여
```

---

## ✅ LV 0 - AWS Budget 설정

월 예산 **$100** 설정 및 80% 초과 시 이메일 알림 구성 완료

> 📸 **AWS Budgets 화면**
> ![budget 생성.png](../../Users/%EB%AC%B8%ED%98%9C%EB%A6%B0/OneDrive/%EC%82%AC%EC%A7%84/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7/budget%20%EC%83%9D%EC%84%B1.png)

> 📸 **AWS Budgets 알림 설정 화면**
> ![budget 알림설정.png](../../Users/%EB%AC%B8%ED%98%9C%EB%A6%B0/OneDrive/%EC%82%AC%EC%A7%84/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7/budget%20%EC%95%8C%EB%A6%BC%EC%84%A4%EC%A0%95.png)

- 예산 금액: **US$100.00**
- 예산 유형: 비용 예산 (월별)
- 알림: 예상 비용 80% 초과 시 이메일 발송

---

## ✅ LV 1 - 네트워크 구축 및 핵심 기능 배포

### 인프라

| 항목 | 값 |
|------|-----|
| VPC | member-vpc (10.0.0.0/16) |
| Public Subnet | 2개 (ap-northeast-2a, 2b) |
| Private Subnet | 2개 (ap-northeast-2a, 2b) |
| EC2 | member-ec2 (t4g.small) |
| EC2 Public IP | **43.203.243.183** |


- #### **EC2 퍼블릭 IP** : `43.203.243.183`

> 📸 **VPC 워크플로우 생성 화면**
> ![VPC](./screenshots/vpc.png)

> 📸 **EC2 인스턴스 화면**
> ![EC2](./screenshots/ec2.png)


### API 목록

- #### **`POST /api/members`** - 팀원 생성

**Request**
```json
{
  "name": "문혜린",
  "age": 25,
  "mbti": "INTJ"
}
```

**Response** `201 Created`
```json
{
  "success": true,
  "code": "201",
  "data": {
    "id": 1,
    "name": "문혜린",
    "age": 25,
    "mbti": "INTJ"
  },
  "timestamp": "2026-03-12T21:12:07.412972071"
}
```

> 📸 **POST /api/members 응답 화면**
> ![POST members](./screenshots/post_members.png)

- #### **`GET /api/members/{id}`** - 팀원 조회

**Response** `200 OK`
```json
{
  "success": true,
  "code": "200",
  "data": {
    "id": 1,
    "name": "문혜린",
    "age": 25,
    "mbti": "INTJ"
  },
  "timestamp": "2026-03-12T21:01:19.763849756"
}
```

> 📸 **GET /api/members/1 응답 화면**
> ![GET members](./screenshots/get_members.png)

### Actuator Health Check

- **URL**: `http://43.203.243.183:8080/actuator/health`

```json
{
  "groups": ["liveness", "readiness"],
  "status": "UP"
}
```

> 📸 **Actuator Health 응답 화면**
> ![Health](./screenshots/health.png)

### Profile 분리

| Profile | DB | 용도 |
|---------|-----|------|
| `local` | H2 (In-Memory) | 로컬 개발 |
| `prod` | MySQL (RDS) | 운영 환경 |

> 📸 **prod 운영환경 설정**
> ![Health](./screenshots/health.png)

> 📸 **prod 운영환경 실행**
> ![Health](./screenshots/health.png)


### 상태 모니터링(Actuator)

> 📸 **health 체크**
> ![Health](./screenshots/health.png)


---

## ✅ LV 2 - DB 분리 및 보안 연결

### RDS 구성

| 항목 | 값 |
|------|-----|
| DB 식별자 | member-db |
| 엔진 | MySQL Community |
| 인스턴스 | db.t4g.micro |
| 리전 | ap-northeast-2 |

> 📸 **RDS 인스턴스 화면**
> ![RDS](./screenshots/rds.png)
> ![RDS](./screenshots/rdsdb.png)

### 보안 그룹 체이닝

RDS 보안 그룹 인바운드 규칙에 IP 주소 대신 **EC2 보안 그룹 ID**만 허용

- 유형: MYSQL/Aurora (TCP 3306)
- 소스: `sg-03e5d603fb0893e2f` (member-ec2-sg)

> 📸 **RDS 보안 그룹 인바운드 규칙**
> ![RDS SG](./screenshots/rds_sg.png)
> ![RDS SG](./screenshots/rds_sg.png)

### Parameter Store

#### SSM Parameter Store에 DB 접속 정보 저장 (`/cloud-project-app/prod/` 경로)

| 파라미터                                  | 설명         |
|---------------------------------------|------------|
| `/cloud-project-app/prod/DB_URL`      | RDS 접속 URL |
| `/cloud-project-app/prod/BUCKETNAME`  | bucket 이름  |
| `/cloud-project-app/prod/DB_USERNAME` | DB 사용자명    |
| `/cloud-project-app/prod/DB_PASSWORD` | DB 비밀번호    |
| `/cloud-project-app/prod/APP_MESSAGE` | 앱 확인용 메시지  |

> 📸 **Parameter Store 화면**
> ![Parameter Store](./screenshots/parameter_store.png)

### Actuator Info

- **URL**: `http://43.203.243.183:8080/actuator/info`

```json
{
  "app": {
    "message": "team name: A3S"
  }
}
```

> 📸 **Actuator Info 응답 화면**
> ![Actuator Info](./screenshots/actuator_info.png)

---

## ✅ LV 3 - 프로필 사진 기능 추가와 권한 관리

### S3 버킷

| 항목 | 값 |
|------|-----|
| 버킷 이름 | project-cloud-prod-profile-files |
| 리전 | ap-northeast-2 (서울) |
| 퍼블릭 액세스 | **모두 차단** |

> 📸 **S3 버킷 화면**
> ![S3 Bucket](./screenshots/s3_bucket.png)

> 📸 **퍼블릭 액세스 차단 설정**
> ![S3 Block Public Access](./screenshots/s3_block.png)

### IAM Role

EC2에 `EC2ParameterStoreAccess` IAM Role 연결 (S3 및 SSM 접근 권한)

> 📸 **IAM Role 연결 화면**
> ![IAM Role](./screenshots/iam_role.png)
> ![IAM Role](./screenshots/iam_role.png)
> ![IAM Role](./screenshots/iam_role.png)
> ![IAM Role](./screenshots/iam_role.png)

### API

#### `POST /api/members/{id}/profile-image` - 프로필 사진 업로드

- MultipartFile(`file`)로 이미지 수신 → S3 업로드 → DB에 URL 저장

**Response** `200 OK`
```json
{
  "key": "uploads/5b75f039-ccff-403b-a133-cc54b8694d68_이세돌 완전체3.png"
}
```

> 📸 **POST /api/members/1/profile-image 응답 화면**
> ![Upload](./screenshots/upload.png)

#### `GET /api/members/{id}/profile-image` - Presigned URL 조회

- S3 Presigned URL 생성 (유효기간: **7일**)
- 클라이언트는 해당 URL로만 이미지 다운로드 가능

**Response** `200 OK`
```json
{
  "url": "https://project-cloud-prod-profile-files.s3.ap-northeast-2.amazonaws.com/uploads/5b75f039-ccff-403b-a133-..."
}
```

> 📸 **GET /api/members/1/profile-image 응답 화면**
> ![Presigned URL](./screenshots/presigned_url.png)

### 📎 Presigned URL

> 🚨 유효기간 7일 설정 완료
> > 📸 **POST /api/members/1/profile-image 응답 화면**
> ![Upload](./screenshots/upload.png)

```
 "url": "https://project-cloud-prod-profile-files.s3.ap-northeast-2.amazonaws.com/uploads/561cebb1-53f2-425a-be3b-196774889d19_%EC%9D%B4%EC%84%B8%EB%8F%8C%20%EC%99%84%EC%A0%84%EC%B2%B44.png?X-Amz-Security-Token=IQoJb3JpZ2luX2VjELv%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDmFwLW5vcnRoZWFzdC0yIkcwRQIgBPTd6MjYXBosnnxJ5qSXHjiLIVmNNdgTsP4YXXq%2Ft7gCIQCQ%2F45p0ShbXERqkBCa8Jg%2B%2FaK4M6IB%2BEHJ8G2FsMME3CrTBQiE%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAAaDDAyNDg2Mzk4MTY3OSIMGrd%2Fvt88fQOAtJvYKqcFvNS%2BF625C%2BcKYfQWAQS9TnaK57INGUCsVpL7omcuqCcm6XjHV9PbpCOTJpcrIHQEfNrRKGYifhF7Ny8lqfIqBz8ZE5sriLoot03S3%2BWfgng%2FrSsNFXo15A6cZ3RaXnJ3jqeiwEVGTUyqLNzFW86%2B83%2Bi%2FwKqn%2Bst9n5OFBxVGNvOU9HjXRlEgAwlNdhsIH%2Fy%2F6V62BEJyP9oW8OjKGS%2BPW%2FWR%2BQUyVhYvusE%2Bgz9KG%2BuzsAPOHILyIE2p7YVvSI0T0zjj4EJqyT8PPXLAIwZqSLhUq5FdTRcm8qQqVifd27bAWH7OoGuWC3O0ag0WPGF2CgnB%2BY5uT%2Bv9%2B8Fmt%2FSbX0XLRXh2sKVnXW4jxnC3zgmU4IW49nkqi95jbU8QiS8yC0FMYhABc9CTbzPpKqbig8WOSzTR5boqpwlt2dDMz64WXI13FBI5pTWqNDkL3TY0bXYVTPofTv%2FFXh7QvFG29HP4x%2BVe59%2BlBvdMMbO%2BOXWnER3RgfPuwKL00hKIbxHJC%2Fqwc7DbIkGUuOXq4eQ7lc49nwZOqgw8bZY4oXEisgiiqdtUZmkC7yThRXT9DLxYeL6ZQ8%2F3Luo8X2OCAvXVi050GS9wgbGxqtc5ulOeYsj7IMb9dqPRP5ibgMcTo9r1AnjYCo9UNhX6eW%2Bu4u9pzf2z0DP8GlXVJJAiqyn9333mSZ01S6vxN8moE%2BNwCzDNOze7Me8xsshfCjwacWeBsxlL0dzIdMNoHx62OOY7X6t632AN5JP%2B2JpIeWxXIGL73ARt5WlAB1hQ6oSfIkcLxOwpFLzN64dF24KyISO9V%2Ftqt6y8JrPfQYhDCcdRkzCIHFwoJ5cLyFhk7XFZw0f114XB4XFcv3FipSgxGYT58LIQbQreaFUticZd1ie%2B94PxmcKPiP9FTCkgs7NBjqxAUbTC3mEldCVPdv%2BHuZ65z%2BEry%2F%2Bda4t%2BD92oSf4qutAysZBfr%2F1laBBMnu6F8u%2FEMzzfsbjuIxj8nN18aFErLbv8dTI2lyvyY9JGAt0HyepNzXr%2BjDL9lcuKkEtQb6W4osgiUrYFM4y2N5y3%2BkgMs94S9QZlHdQ%2FFettjdtmt3saxLAeMbmHUEx%2BsW25LpQ5mfn4CWXMI2kI189ZZbELA3DSJbJiTdaYM1FXl%2BWGvgHTw%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260313T033325Z&X-Amz-SignedHeaders=host&X-Amz-Credential=ASIAQLSQCIBXW3TOJWNX%2F20260313%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=330ed339b09cee866ad2cd6277c6e0ffac695f31b000ec496e1479c745a6772e"
```

> ⚠️ **만료 시간**: 과제 제출일 기준 7일 후  
> > 📸 **POST /api/members/1/profile-image 응답 화면**
> ![Upload](./screenshots/upload.png)

---

## 📁 프로젝트 구조

```
src/main/java/jpa/basic/projectcloud/
├── config/
│   └── Logger.java          # AOP 기반 API 요청 로깅
├── exception/
│   ├── ApiResponse.java
│   ├── CustomException.java
│   ├── ErrorCode.java
│   ├── ErrorResponse.java
│   ├── FieldError.java
│   └── GlobalExceptionHandler.java
└── user/
    ├── controller/
    │   └── UserController.java
    ├── dto/
    │   ├── request/CreateUserRequest.java
    │   └── response/UserResponse.java
    ├── entity/User.java
    ├── repository/UserRepository.java
    └── service/UserService.java

src/main/resources/
├── application.yml           # 공통 설정
├── application-local.yml     # H2 로컬 설정
└── application-prod.yml      # RDS + Parameter Store 운영 설정
```
