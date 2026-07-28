# moneylog (백엔드)

가계부 서비스 백엔드 API 서버입니다.

## 기술 스택

- Java 17, Spring Boot 3.3.2
- Spring Data JPA, MySQL 8.0 (운영) / H2 (로컬)
- Spring Security + JWT (Access/Refresh Token)
- springdoc-openapi (Swagger UI)
- Docker, GitHub Actions (CI/CD), Nginx

## 로컬 실행

```bash
./gradlew bootRun
```

기본적으로 `local` 프로파일(H2 인메모리 DB)로 실행됩니다.

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

## Docker Compose로 실행 (MySQL 연동)

```bash
docker compose up -d --build
```

`.env` 파일에 아래 값을 채워야 합니다.

```
MYSQL_ROOT_PASSWORD=
MYSQL_PASSWORD=
JWT_SECRET=          # 최소 32바이트 이상 권장
CORS_ALLOWED_ORIGINS=http://localhost:5173
```

## API 도메인

- `auth` — 회원가입, 로그인, 토큰 재발급, 로그아웃
- `category` — 카테고리 조회
- `transaction` — 거래내역 등록/조회/검색/CSV 내보내기/수정/삭제
- `statistics` — 월별/트렌드 통계

## 배포

`main` 브랜치에 push되면 GitHub Actions가 Docker 이미지를 빌드해 GHCR에 올리고, EC2에 SSH로 접속해 `docker compose pull && up -d`로 재기동합니다. 실제 서버 구성(mysql + app + nginx)은 `docker-compose.yml`을 참고하세요. 프론트엔드는 별도 저장소([moneylog-frontend](https://github.com/justicechan1/moneylog-frontend))에서 빌드되어 같은 서버에 `frontend` 컨테이너로 함께 배포됩니다.
