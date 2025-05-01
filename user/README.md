# Trueshot User Service

This is a Spring Boot microservice for user management, following system, and JWT-based authentication.

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

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/signup` | Register new user | ❌ |
| POST | `/api/auth/authenticate` | Login & get JWT token | ❌ |
| POST | `/api/follow/follow/{followingId}` | Follow a user | ✅ |
| POST | `/api/follow/unfollow/{followingId}` | Unfollow a user | ✅ |
| GET  | `/api/follow/suggestions` | Get follow suggestions | ✅ |
| GET  | `/api/follow/following` | List following users | ✅ |
| GET  | `/api/follow/followers` | List followers | ✅ |

---

## 🧪 Example Requests

### 🔸 Signup

```bash
curl -X POST http://localhost:8087/api/auth/signup  -H "Content-Type: application/json"  -d '{"name": "testuser", "password": "testpass", "roles": "USER"}'
```

### 🔸 Authenticate

```bash
curl -X POST http://localhost:8087/api/auth/authenticate  -H "Content-Type: application/json"  -d '{"name": "testuser", "password": "testpass"}'
```

Returns JWT token.

### 🔸 Follow a user

```bash
curl -X POST http://localhost:8087/api/follow/follow/{followingId}  -H "Authorization: Bearer <your_token>"
```

### 🔸 Unfollow a user

```bash
curl -X POST http://localhost:8087/api/follow/unfollow/{followingId}  -H "Authorization: Bearer <your_token>"
```

### 🔸 Get follow suggestions

```bash
curl -X GET http://localhost:8087/api/follow/suggestions  -H "Authorization: Bearer <your_token>"
```

### 🔸 Get following users

```bash
curl -X GET http://localhost:8087/api/follow/following  -H "Authorization: Bearer <your_token>"
```

### 🔸 Get followers

```bash
curl -X GET http://localhost:8087/api/follow/followers  -H "Authorization: Bearer <your_token>"
```

---

## 📚 Swagger UI

Visit: [http://localhost:8087/swagger-ui/index.html](http://localhost:8087/swagger-ui/index.html)

---

## ⚙️ Environment Config

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