# 🩺 VitalCare API

VitalCare API is a Spring Boot backend system for a health monitoring platform.
It enables users to track vital health indicators such as blood pressure, BMI,
heart rate, and lifestyle habits.

This project is developed as part of a **Bachelor Thesis**.

---

## 🚀 Features

- User authentication (JWT)
- Health vitals management
  - Blood Pressure
  - Heart Rate
  - BMI
  - Lifestyle habits
- RESTful API design
- Input validation & global exception handling
- Swagger (OpenAPI) documentation
- Scalable layered architecture

---

## 🛠️ Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security (JWT)
- Hibernate
- PostgreSQL
- Gradle
- Swagger (OpenAPI)

---

## 📁 Project Structure

```
src/main/java
├── controller
├── service
│   └── impl
├── repository
├── entity
├── dto
├── mapper
├── exception
├── config
└── security
```

---

## ⚙️ Configuration

Edit `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vital_care
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
```

---

## ▶️ Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

Server runs at:
```
http://localhost:8080
```

---

## 📄 API Documentation

Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

---

## 🔐 Authentication

Uses JWT authentication.

Request header:
```http
Authorization: Bearer <token>
```

---

## 🧪 Testing

```bash
gralde test
```

---

## 👨‍🎓 Academic Information

- Project Type: Bachelor Thesis
- Duration: 4 months
- Team Members: 3
- Supervisor: 1 academic coach

---

## 📌 Future Enhancements

- Role-based access control
- Health analytics & reports
- Notifications & alerts
- Wearable device integration

---

## 📄 License

This project is developed for academic purposes only.
