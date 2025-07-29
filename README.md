# ⚽ Football Standings App

This repository contains a full-stack football standings application composed of:

- **Backend (`football-service`)**: A Spring Boot application that interacts with a football data API.
- **Frontend (`ui-service`)**: An Angular single-page application for displaying country, league, and team standings.

---

## 📐 Design & Implementation

### 🔧 Backend (`football-service`)

The backend provides REST endpoints to retrieve:

- Countries
- Leagues for a selected country
- Standings for a selected league

#### Features:

- **Resilience**: Uses Resilience4j for `@CircuitBreaker` and `@Retry` to handle API failures gracefully.
- **Caching**: Implements Spring Cache to reduce redundant external API calls.
- **Fallback**: Provides mock/fallback data if the external API is unavailable.
- **Layered Architecture**: Separates controller, service, and client responsibilities.


## 📊 Architecture Flow

> Diagram created using [draw.io](https://draw.io) – saved as `docs/architecture-diagram.png`.

```plaintext
User
  |
  v
Angular UI (ui-service)
  |
  v
Spring Boot Backend (football-service)
  |
  v
External Football API

External API
     ↓ fails
FallbackService (Mocked data returned)
