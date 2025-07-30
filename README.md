# Football Standings Backend

This is the Spring Boot backend service for the Football Standings app.  
It provides REST APIs to fetch countries, leagues, and standings from external football APIs with JWT-based security.

---

## Tech Stack

- **Framework:** Spring Boot 3
- **Language:** Java 17
- **Security:** Spring Security + JWT
- **Build Tool:** Maven
- **API Integration:** Feign Client
- **Caching:** Spring Cache
- **Deployment:** Dockerized

---

## Design Patterns / Principles

- **SOLID Principles**
- **Strategy Pattern** – External API providers
- **Separation of Concerns** – Controller, Service, Fallback, Config
- **HATEOAS-Ready** design
- **12-Factor App** adherence

---

## Prerequisites

- JDK 17+
- Maven
- Docker Desktop (for containerized run)
- Internet access (for live football API integration)

---

## Steps to Run Locally

```bash
cd backend
./mvnw clean install
java -jar target/football-standings-0.0.1-SNAPSHOT.jar

```

## Steps to Run with Docker

```bash
docker build -t football-standings:v1.0.0 .
docker run -p 8080:8080 football-standings:v1.0.0

```