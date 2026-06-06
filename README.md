# 도서 관리 시스템 백엔드 (miniproject05)
KT 에이블스쿨 9기 미니프로젝트 5차 백엔드 저장소입니다.

본 프로젝트는 React 프론트엔드 애플리케이션(mini-project04)과 연동하여 동작하는 Spring Boot 기반의 도서 관리 백엔드 API 서버입니다.

## 기술 스택 (Tech Stack)
* **Language**: Java 17
* **Framework**: Spring Boot 4.x (Spring Boot 3.x 이상 표준 적용)
* **Database**: H2 Database (인메모리 모드)
* **ORM**: Spring Data JPA & Hibernate
* **Utilities**: Lombok, Spring Validation

## 프로젝트 아키텍처 (Layered Architecture)
본 프로젝트는 관심사 분리와 가독성을 위해 **계층형 아키텍처**로 설계되었습니다.
* **Controller**: HTTP 요청 수신, API 라우팅, CORS 설정 적용 및 DTO/객체 변환 (`@RestController`, `@CrossOrigin`)
* **Service**: 핵심 비즈니스 로직 처리, 트랜잭션 범위 지정 및 데이터 수정 처리 (`@Service`, `@Transactional`)
* **Repository**: Spring Data JPA를 활용한 DB CRUD 및 쿼리 메서드 검색 정의 (`@Repository`, `JpaRepository`)
* **Domain**: 데이터베이스 테이블 매핑용 영속성 객체 정의 및 제약 조건 설정 (`@Entity`, `@NotBlank`, `@Builder`)
* **Exception**: 애플리케이션 전역 예외 처리 및 적절한 HTTP 상태 코드 응답 변환 (`@RestControllerAdvice`, `@ExceptionHandler`)

## ⚡ 주요 구현 기능
1. **도서 CRUD API 제공**:
   - 전체 목록 조회 (`GET /books`) - H2 DB 조회 적용
   - 상세 조회 (`GET /books/{id}`) - 사용자 정의 예외 처리 연동
   - 도서 등록 (`POST /books`) - `@Valid` 기반의 데이터 무결성 검증 추가
   - 도서 수정 (`PATCH /books/{id}`) - 전달된 부분 필드에 대한 null 체크 부분 수정 적용
   - 도서 삭제 (`DELETE /books/{id}`) - 삭제 성공 시 204 No Content 표준 응답 반환
2. **CORS 에러 해소**: `@CrossOrigin` 설정을 통해 프론트엔드 Vite 개발 서버 포트(`5173`)로부터의 요청 허용
3. **글로벌 예외 처리**: 책 미존재 시 `404 Not Found`, 유효성 검증 실패 시 `400 Bad Request` 에러 자동 변환 반환
4. **H2 인메모리 초기화**: 서버 기동 시 롬복 빌더를 활용하여 테스트용 샘플 도서 5권 자동 등록

## 로컬 서버 실행 방법
### 의존성 빌드 및 실행
프로젝트 루트 폴더에서 아래 명령어를 실행하여 백엔드 API 서버를 기동합니다.
```bash
./gradlew bootRun
```
* API 기본 Endpoint: `http://localhost:8080`
* H2 Database Console URL: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:bookdb`
  - User Name: `sa`
  - Password: `1234`
