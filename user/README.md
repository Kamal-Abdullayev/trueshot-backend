# Trueshot User Service

This is a Spring Boot microservice for user management and JWT-based authentication.

## 🔧 Technologies Used

- Java 21
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Token)
- Maven
- Docker (for DB)
- Lombok
- Swagger (SpringDoc OpenAPI)

---

## 🚀 Running the App

### 1. Start PostgreSQL (via Docker)

```bash
docker-compose up -d
```

PostgreSQL runs at `localhost:5433`, DB name is `trueshot_db`.

### 2. Build and Run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

App will start at: [http://localhost:8087](http://localhost:8087)

---

## 🔐 API Endpoints

| Method | Endpoint                  | Description            | Auth Required |
|--------|---------------------------|------------------------|---------------|
| POST   | `/api/auth/signup`        | Register new user      | ❌            |
| POST   | `/api/auth/authenticate`  | Login & get JWT token  | ❌            |


---

## 🧪 Example Requests

### 🔸 Signup

```bash
curl -X POST http://localhost:8087/api/auth/signup \
 -H "Content-Type: application/json" \
 -d '{"name": "testuser", "password": "testpass", "roles": "USER"}'
```

### 🔸 Authenticate

```bash
curl -X POST http://localhost:8087/api/auth/authenticate \
 -H "Content-Type: application/json" \
 -d '{"name": "testuser", "password": "testpass"}'
```

Returns a JWT token.

---

## 📚 Swagger UI

Visit: [http://localhost:8087/swagger-ui/index.html](http://localhost:8087/swagger-ui/index.html)

---

## ⚙️ Environment Config

Update `application.properties` as needed:

```properties
server.port=8087
spring.datasource.url=jdbc:postgresql://localhost:5433/trueshot_db
spring.datasource.username=admin
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

---

## ✍️ License

MIT License
