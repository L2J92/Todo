# Todo REST API

Spring Boot와 Spring Data JPA로 만든 할 일 관리 REST API입니다. 회원 가입이나 로그인 없이 할 일을 생성하고 조회·수정·삭제할 수 있으며, 완료 여부도 변경할 수 있습니다.

## 기술 스택

- Java 21
- Spring Boot 3.5.16
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 Database
- springdoc-openapi(Swagger UI)
- Gradle

## 실행 방법

### 준비 사항

- JDK 21이 설치되어 있어야 합니다.
- 별도의 데이터베이스 설치는 필요하지 않습니다. 애플리케이션이 H2 데이터베이스 파일을 자동으로 생성합니다.

### 실행

프로젝트 최상위 디렉터리에서 다음 명령을 실행합니다.

macOS/Linux:

```bash
./gradlew bootRun
```

Windows:

```powershell
.\gradlew.bat bootRun
```

서버는 `http://localhost:8090`에서 실행됩니다.

데이터는 프로젝트의 `data/todo.mv.db` 파일에 저장되므로 서버를 다시 실행해도 유지됩니다.

### API 문서 및 H2 콘솔

- Swagger UI: `http://localhost:8090/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8090/v3/api-docs`
- H2 Console: `http://localhost:8090/h2-console`

H2 콘솔 접속 정보:

| 항목 | 값 |
| --- | --- |
| JDBC URL | `jdbc:h2:file:./data/todo` |
| User Name | `sa` |
| Password | 없음 |

## API 명세

모든 요청과 응답의 `Content-Type`은 `application/json`입니다. 날짜와 시간은 ISO 8601 형식으로 반환됩니다.

### 엔드포인트 요약

| 기능 | HTTP 메서드 | 주소 | 성공 상태 코드 |
| --- | --- | --- | --- |
| 할 일 생성 | `POST` | `/todo` | `200 OK` |
| 할 일 목록 조회 | `GET` | `/todo` | `200 OK` |
| 할 일 단건 조회 | `GET` | `/todo/{id}` | `200 OK` |
| 할 일 수정 및 완료 상태 변경 | `PUT` | `/todo/{id}` | `204 No Content` |
| 할 일 삭제 | `DELETE` | `/todo/{id}` | `200 OK` |

### 1. 할 일 생성

```http
POST /todo
```

요청 본문:

```json
{
  "title": "Spring 과제 제출하기"
}
```

검증 규칙:

- `title`은 필수이며 빈 문자열이나 공백만 입력할 수 없습니다.
- `title`은 최대 100자입니다.
- 새 할 일의 `completed`는 서버에서 항상 `false`로 설정합니다.

성공하면 `200 OK`와 빈 본문을 반환합니다.

### 2. 할 일 목록 조회

```http
GET /todo?page=0&size=10&sort=createdAt,desc
```

Spring Data의 페이지 기능을 사용합니다.

| 쿼리 파라미터 | 설명 | 예시 |
| --- | --- | --- |
| `page` | 페이지 번호이며 0부터 시작 | `0` |
| `size` | 한 페이지에 표시할 개수 | `10` |
| `sort` | 정렬할 필드와 방향 | `createdAt,desc` |

응답 예시:

```json
{
  "content": [
    {
      "id": 1,
      "title": "Spring 과제 제출하기",
      "completed": false,
      "createdAt": "2026-09-23T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "first": true,
  "last": true,
  "empty": false
}
```

Spring Data가 페이지 응답에 `pageable`, `sort`, `numberOfElements` 등의 부가 필드를 추가할 수 있습니다.

### 3. 할 일 단건 조회

```http
GET /todo/{id}
```

응답 예시:

```json
{
  "id": 1,
  "title": "Spring 과제 제출하기",
  "completed": false,
  "createdAt": "2026-09-23T10:30:00"
}
```

존재하지 않는 ID를 조회하면 `404 Not Found`를 반환합니다.

### 4. 할 일 수정 및 완료 상태 변경

```http
PUT /todo/{id}
```

요청 본문:

```json
{
  "title": "Spring 과제 제출 완료",
  "completed": true
}
```

제목과 완료 여부를 함께 전달하여 할 일 전체를 수정합니다. 성공하면 `204 No Content`와 빈 본문을 반환합니다. 존재하지 않는 ID를 수정하면 `404 Not Found`를 반환합니다.

### 5. 할 일 삭제

```http
DELETE /todo/{id}
```

성공하면 `200 OK`와 빈 본문을 반환합니다. 존재하지 않는 ID를 삭제하면 `404 Not Found`를 반환합니다.

## 오류 응답

오류가 발생하면 HTTP 상태 코드와 함께 다음과 같은 공통 형식으로 응답합니다.

```json
{
  "code": "TODO_NOT_FOUND",
  "message": "Todo not found"
}
```

| HTTP 상태 코드 | code 예시 | 발생 상황 |
| --- | --- | --- |
| `400 Bad Request` | `VALIDATION_ERROR` | 제목이 비어 있거나 100자를 초과한 경우 |
| `400 Bad Request` | `INVALID_REQUEST_BODY` | JSON 문법이나 요청 본문의 타입이 올바르지 않은 경우 |
| `400 Bad Request` | `INVALID_PARAMETER` | URL 경로나 요청 파라미터의 타입이 올바르지 않은 경우 |
| `404 Not Found` | `TODO_NOT_FOUND` | 요청한 ID의 할 일이 없는 경우 |
| `500 Internal Server Error` | `INTERNAL_SERVER_ERROR` | 서버에서 예상하지 못한 오류가 발생한 경우 |

## 설계 설명

### API 주소와 HTTP 메서드

할 일이라는 자원을 `/todo`로 표현하고 HTTP 메서드로 동작을 구분했습니다. `POST`는 생성, `GET`은 조회, `PUT`은 전체 수정, `DELETE`는 삭제에 사용했습니다. 단건 자원은 `/todo/{id}`로 표현하여 어떤 할 일을 대상으로 하는지 주소에서 확인할 수 있도록 했습니다.

목록 조회에는 `Pageable`을 적용했습니다. 데이터가 많아져도 한 번에 모든 행을 가져오지 않고 필요한 범위만 조회할 수 있습니다.

### 상태 코드

- 정상 조회와 생성·삭제에는 `200 OK`를 사용했습니다.
- 수정 성공 후에는 반환할 본문이 없으므로 `204 No Content`를 사용했습니다.
- 잘못된 입력에는 `400 Bad Request`를 사용합니다.
- 존재하지 않는 자원에는 `404 Not Found`를 사용합니다.
- 예상하지 못한 서버 오류에는 `500 Internal Server Error`를 사용합니다.

### DTO 분리

JPA 엔티티를 요청과 응답에 직접 노출하지 않고 생성 요청, 수정 요청, 응답 DTO를 별도로 사용했습니다. 이를 통해 데이터베이스 구조와 외부 API 구조가 직접 결합되는 것을 방지하고, 요청 입력값 검증과 응답 필드 관리를 독립적으로 할 수 있습니다.

### H2 데이터베이스 선택 이유

과제 실행자가 MySQL이나 PostgreSQL을 별도로 설치하고 계정을 설정하지 않아도 바로 실행할 수 있도록 H2를 선택했습니다. 인메모리 방식이 아닌 파일 방식을 사용하여 애플리케이션을 종료해도 저장한 할 일이 유지됩니다.

### 오류 처리

`@RestControllerAdvice`를 사용해 예외 처리를 한곳에 모았습니다. API 이용자가 오류 종류와 관계없이 항상 `code`와 `message` 필드를 확인할 수 있도록 공통 `ErrorResponse` DTO를 사용합니다.

## 실행 결과

다음 예시는 서버를 실행한 상태에서 순서대로 호출할 수 있습니다. 아래 응답의 ID와 생성 시각은 실행 환경에 따라 달라질 수 있습니다.

### 1. 할 일 생성

요청:

```bash
curl -i -X POST "http://localhost:8090/todo" \
  -H "Content-Type: application/json" \
  -d '{"title":"Spring 과제 제출하기"}'
```

응답:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

### 2. 목록 조회

요청:

```bash
curl -i "http://localhost:8090/todo?page=0&size=10"
```

응답 본문의 주요 내용:

```json
{
  "content": [
    {
      "id": 1,
      "title": "Spring 과제 제출하기",
      "completed": false,
      "createdAt": "2026-09-23T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### 3. 완료 처리

요청:

```bash
curl -i -X PUT "http://localhost:8090/todo/1" \
  -H "Content-Type: application/json" \
  -d '{"title":"Spring 과제 제출하기","completed":true}'
```

응답:

```http
HTTP/1.1 204 No Content
```

완료 상태 확인:

```bash
curl -i "http://localhost:8090/todo/1"
```

```json
{
  "id": 1,
  "title": "Spring 과제 제출하기",
  "completed": true,
  "createdAt": "2026-09-23T10:30:00"
}
```

### 4. 삭제

요청:

```bash
curl -i -X DELETE "http://localhost:8090/todo/1"
```

응답:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

### 5. 잘못된 제목 요청 — 400

요청:

```bash
curl -i -X POST "http://localhost:8090/todo" \
  -H "Content-Type: application/json" \
  -d '{"title":"   "}'
```

응답 예시:

```http
HTTP/1.1 400 Bad Request
Content-Type: application/json
```

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Title cannot be blank"
}
```

### 6. 존재하지 않는 할 일 요청 — 404

요청:

```bash
curl -i "http://localhost:8090/todo/999999"
```

응답:

```http
HTTP/1.1 404 Not Found
Content-Type: application/json
```

```json
{
  "code": "TODO_NOT_FOUND",
  "message": "Todo not found"
}
```

## 프로젝트 구조

```text
src
├─ main
│  ├─ java/com/example/todo
│  │  ├─ controller   # HTTP 요청과 응답 처리
│  │  ├─ service      # 비즈니스 로직
│  │  ├─ repository   # 데이터 접근
│  │  ├─ entity       # JPA 엔티티
│  │  ├─ dto          # 요청·응답 DTO
│  │  └─ exception    # 예외와 공통 오류 응답
│  └─ resources
│     └─ application.yaml
└─ test               # 테스트 코드
```
