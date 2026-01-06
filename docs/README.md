# Wiiroogaatee12 - Dutch Election Platform

## Introduction

### Project Overview

Dit project is een **fullstack webapplicatie** voor het analyseren en interactief werken met Nederlandse verkiezingsgegevens. De applicatie combineert historische verkiezingsdata met moderne AI-functionaliteit om burgers te helpen bij het maken van geïnformeerde stemkeuzes.

### Wat doet deze applicatie?

De applicatie biedt:

- **Verkiezingsdata Analyse**: Visuele weergave van historische verkiezingsresultaten per gemeente, kieskring en landelijk niveau
- **Interactieve Stemhulp (Voting Guide)**: Vragenlijst die gebruikers helpt hun politieke voorkeuren te identificeren
- **Community Engagement**: Gebruikers kunnen interacteren met polls, reageren op verkiezingsgerelateerde content en discussies voeren
- **Admin Moderatie**: Content moderation via Google Gemini API voor het filteren van ongepaste inhoud
- **Real-time Visualisaties**: Kaarten (Leaflet), grafieken (Chart.js) en interactieve datavisualisaties

### Technische Stack

- **Backend**: Spring Boot 3.5.5 (Java 21), PostgreSQL (Supabase), JWT authenticatie
- **Frontend**: Vue 3 (Composition API), TypeScript, Pinia, TailwindCSS
- **External APIs**: Kiesraad API (verkiezingsdata), Google Gemini API (AI moderation)
- **Deployment**: Docker + Docker Compose

### Target Audience

Deze documentatie is bedoeld voor:
- **Nieuwe ontwikkelaars** die het project overnemen en verder ontwikkelen
- **Maintenance teams** die de applicatie moeten onderhouden en deployen
- **Product owners** die inzicht willen in de architectuur en design decisions

---

## API Documentation

### Swagger UI

De backend API is volledig gedocumenteerd met **Swagger UI** (via SpringDoc OpenAPI). Alle REST endpoints, request/response schemas en authentication flows zijn beschikbaar in een interactieve interface.

#### Hoe toegang te krijgen tot Swagger UI

1. **Start de backend applicatie**:
   ```bash
   cd election-backend
   ./mvnw spring-boot:run
   ```
   Of via Docker:
   ```bash
   docker-compose up backend
   ```

2. **Open Swagger UI in je browser**:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```
   
   > **Let op**: Als je de applicatie via Docker Compose draait met custom ports, gebruik dan:
   > ```
   > http://localhost:18080/swagger-ui/index.html
   > ```

#### Wat vind je in Swagger UI?

De Swagger UI documenteert alle beschikbare endpoints, gegroepeerd per controller:

- **Auth Controller** (`/api/auth/*`): Login, registratie, refresh tokens, logout
- **Poll Controller** (`/api/polls/*`): Polls aanmaken, stemmen, resultaten ophalen
- **Election Controller** (`/api/elections/*`): Verkiezingsdata ophalen (jaren, partijen, resultaten)
- **Admin Moderation Controller** (`/api/admin/moderation/*`): Content moderation en review
- **Municipality Controller** (`/api/municipalities/*`): Gemeentegegevens en resultaten
- **Party Controller** (`/api/parties/*`): Partijinformatie en stemresultaten

#### API Endpoints Testen via Swagger

1. **Authenticatie**: 
   - Voor protected endpoints heb je een JWT token nodig
   - Test eerst `POST /api/auth/login` met valide credentials
   - Kopieer het access token uit de response
   - Klik op de "Authorize" knop bovenaan de Swagger UI
   - Plak het token in het veld en klik "Authorize"

2. **Endpoints Uitproberen**:
   - Klik op een endpoint om deze uit te klappen
   - Klik "Try it out"
   - Vul de vereiste parameters in
   - Klik "Execute" om de request te versturen
   - Bekijk de response inclusief status code, headers en body

#### OpenAPI Specificatie

De volledige OpenAPI specificatie is beschikbaar in JSON formaat:
```
http://localhost:8080/v3/api-docs
```

Deze kan worden geïmporteerd in tools zoals Postman, Insomnia of andere API clients.

#### SpringDoc OpenAPI Configuratie

De Swagger UI is geconfigureerd via de SpringDoc OpenAPI library:

**Dependency in `pom.xml`**:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.4</version>
</dependency>
```

De configuratie gebeurt automatisch via Spring Boot auto-configuration. Custom configuratie kan worden toegevoegd in een `@Configuration` class indien nodig.

---

### 1. tech-stack.md
Comprehensive reference of all technologies and libraries in the project.

Contains:
- Backend: Spring Boot 3.5.5, Java 21, PostgreSQL, JWT, OkHttp, JSoup
- Frontend: Vue 3, TypeScript, Pinia, TailwindCSS, Chart.js, Leaflet
- External Services: Google Gemini API, Kiesraad API
- Infrastructure: Docker

---

### 2. design-decisions.md
Detailed analysis of all architecture and design choices.

Contains:
- Backend Architecture: Layered structure, Entity-DTO pattern, JWT auth, XML parsing
- Frontend Architecture: Vue 3 Composition API, Pinia state management, Custom API client
- Data Flow: Collections design, performance patterns
- Security: Password hashing, content moderation
- Testing: Unit test strategy

---

### 3. DOCUMENTATIE_ANALYSE.md
Summary report of documentation analysis and findings.

---

## Existing Documentation

| File | Description |
|------|-------------|
| starting.md | Getting started guide |
| index.md | Project overview |
| samenwerkingscontract.md | Team agreement |
| retrospectives.md | Sprint retrospectives |
| UML.md | UML diagrams |
| daily standups/sprint_2.md | Daily standup notes |

---

## Navigation Guide

### For Onboarding
1. Read tech-stack.md for technology overview
2. Read design-decisions.md for architecture understanding
3. See DOCUMENTATIE_ANALYSE.md for complete context

### For Backend Development
1. Architecture → design-decisions.md (Backend Architecture section)
2. Technologies → tech-stack.md (Backend section)
3. Future improvements in design-decisions.md

### For Frontend Development
1. Architecture → design-decisions.md (Frontend Architecture section)
2. Technologies → tech-stack.md (Frontend section)
3. Styling approach → TailwindCSS & Component Variants section

### For Code Review or Refactoring
1. Design rationale → design-decisions.md
2. Technology choices → tech-stack.md
3. Improvement opportunities → Future Improvements section

---

## Documentation Statistics

| Document | Coverage |
|----------|----------|
| tech-stack.md | All backend and frontend technologies with rationale |
| design-decisions.md | All architecture patterns and design choices |
| DOCUMENTATIE_ANALYSE.md | Summary and navigation guide |

---

## Topics Covered

Completed:
- Technology stack analysis
- Design decision documentation
- Architectural pattern analysis
- Security considerations
- Performance optimization approaches
- Testing strategies

Remaining (out of scope):
- Project introduction
- Architecture diagrams
- API documentation
- Installation guide
- Deployment procedures


