# 직원 긴급 연락망 API

## API 리스트
#### 직원 전체 페이징 조회
- GET http://localhost:8080/api/employee?page={page}&pageSize={pageSize}
#### 직원 이름 조회
- GET http://localhost:8080/api/employee/{name}
#### 직원 데이터 저장
- POST http://localhost:8080/api/employee

## OpenAPI Swagger UI
- http://localhost:8080/swagger-ui.html

## Log
- /logs 하위 경로에 생성
- tomcat accesslog : access_log-{yyyy-MM-dd}.log
- application log : app-{yyyy-MM-dd}.log