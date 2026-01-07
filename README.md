# Wiiroogaatee12 - Dutch Election Platform

[![GitLab](https://img.shields.io/badge/GitLab-CI%2FCD-orange)](https://gitlab.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.5.18-4FC08D)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-21-red)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue)](https://www.postgresql.org/)

> **Een moderne fullstack webapplicatie voor het analyseren en interactief werken met Nederlandse verkiezingsgegevens**

---

## Introductie

### Wat doet deze applicatie?

Wiiroogaatee12 is een **fullstack webapplicatie** die burgers helpt bij het maken van geïnformeerde stemkeuzes door historische verkiezingsdata te combineren met moderne AI-functionaliteit. De applicatie biedt tools voor data-analyse, interactieve stemhulp, en community engagement rond Nederlandse verkiezingen.

### Kernfunctionaliteiten

- **Verkiezingsdata Analyse**: Visuele weergave van historische verkiezingsresultaten per gemeente, kieskring en landelijk niveau
- **Interactieve Stemhulp (Voting Guide)**: Vragenlijst die gebruikers helpt hun politieke voorkeuren te identificeren  
- **Community Engagement**: Discussieplatform waar gebruikers kunnen interacteren via polls en discussies
- **AI Content Moderatie**: Automatische filtering van ongepaste inhoud via Google Gemini API
- **Real-time Visualisaties**: Interactieve kaarten (Leaflet) en grafieken (Chart.js) voor datavisualisatie

### Technische Stack (Kort)

- **Backend**: Spring Boot 3.5.5 (Java 21), PostgreSQL (Supabase), JWT authenticatie
- **Frontend**: Vue 3 (Composition API), TypeScript, Pinia, TailwindCSS
- **External APIs**: Kiesraad API (verkiezingsdata), Google Gemini API (AI moderation)
- **Deployment**: Docker + Docker Compose

### Doelgroep van deze Documentatie

Deze documentatie is bedoeld voor:
- **Nieuwe ontwikkelaars** die het project overnemen en verder ontwikkelen
- **Maintenance teams** die de applicatie moeten onderhouden en deployen
- **Product owners** die inzicht willen in de architectuur en design decisions

---

## Documentatie Overzicht

Deze README dient als centrale hub voor alle projectdocumentatie. Alle gedetailleerde informatie is onderverdeeld in aparte documenten:

### [Architectuur](docs/architecture.md)
Volledige beschrijving van de applicatie-architectuur, inclusief:
- High-level systeemdiagram
- Backend layered architecture (Controllers  Services  Repositories  Entities)
- Frontend component hiërarchie en structuur
- **Class diagrams**: Entity models voor User, Election, Voting Guide, en Discussion domains
- **ERD (Entity Relationship Diagram)**: Complete database schema
- Data flow architectuur (verkiezingsdata import, content moderation)
- Authenticatie flow (JWT + refresh tokens)
- API endpoints overzicht

**[Bekijk volledige architectuur documentatie ](docs/architecture.md)**

---

### [Tech Stack & Libraries](docs/tech-stack.md)
Comprehensive overzicht van alle gebruikte technologieën en libraries:

**Backend:**
- Spring Boot 3.5.5, Java 21, Spring Data JPA
- PostgreSQL (via Supabase), HikariCP connection pooling
- JWT authenticatie (JJWT 0.12.6), BCrypt password hashing
- OkHttp HTTP client, JSoup HTML/XML parsing

**Frontend:**
- Vue 3.5.18, TypeScript, Vite build tool
- Pinia state management, Vue Router
- TailwindCSS + PrimeVue + Reka UI components
- Chart.js, Leaflet, Unovis voor data visualisaties
- Custom API client met JWT refresh logic

**External Services:**
- Google Gemini API (AI text moderation)
- Kiesraad API (verkiezingsdata)

**[Bekijk volledige tech stack documentatie ](docs/tech-stack.md)**

---

### [Design Decisions](docs/design-decisions.md)
Gedetailleerde uitleg van alle architecturale en design keuzes:

**Backend Patterns:**
- Layered Architecture Pattern (waarom gekozen?)
- Entity-DTO separation voor security en API versioning
- JWT + Refresh Token authenticatie flow
- Composite keys voor election data (year + partyId)
- XML parsing met Transformer pattern
- HikariCP connection pooling configuratie

**Frontend Patterns:**
- Vue 3 Composition API (vs Options API)
- Pinia state management (vs Vuex)
- Custom API client (vs axios)
- TailwindCSS + class-variance-authority voor component styling
- Router guards voor authentication

**Data Structures:**
- HashMap voor vote aggregation (performance)
- Set vs List keuzes voor collections

**Security:**
- BCrypt password hashing
- Google Gemini API voor content moderation
- CORS whitelist configuratie

**[Bekijk volledige design decisions documentatie ](docs/design-decisions.md)**

---

### [Deployment](docs/deployment.md)
Complete deployment handleiding voor alle omgevingen:

**Deployment Opties:**
1. **Docker Compose** (Aanbevolen voor productie)
   - Complete setup met `docker-compose up`
   - Environment variabelen configuratie
   - Health checks en monitoring

2. **Handmatige Deployment**
   - Backend: Maven build + JAR deployment
   - Frontend: Vite build + Nginx/Apache setup
   - Systemd service configuratie (Linux)

3. **GitLab CI/CD**
   - Automatische builds en deployments
   - MkDocs documentatie pipeline

**Database Setup:**
- Migreren van Supabase naar eigen PostgreSQL
- Schema export/import procedures
- JPA/Hibernate configuratie opties

**Environment Variabelen:**
- Complete referentie van alle verplichte en optionele variabelen
- Backend (Spring Boot) configuratie
- Frontend (Vite) configuratie

**Troubleshooting:**
- Veelvoorkomende problemen en oplossingen
- Logging configuratie
- Health checks

**[Bekijk volledige deployment documentatie ](docs/deployment.md)**

---

## Quick Start - Installatie & Setup

### Vereisten

Zorg dat je de volgende software geïnstalleerd hebt:

| Software | Minimale Versie | Aanbevolen |
|----------|-----------------|------------|
| Java | 21 | 21 LTS |
| Node.js | 20.19.0 | 22.12.0+ |
| Maven | 3.9.x | 3.9.4 |
| Docker | 20.10+ | 24.0+ (optioneel) |
| Docker Compose | 2.20+ | 2.24+ (optioneel) |

### Optie 1: Docker Compose (Aanbevolen)

**Stap 1: Clone de repository**
```bash
git clone <repository-url>
cd wiiroogaatee12
```

**Stap 2: Configureer environment variabelen**

Maak een `.env` bestand in de project root:

```bash
# Database configuratie
DB_URL=jdbc:postgresql://your-host:5432/postgres
DB_USERNAME=your-username
DB_PASSWORD=your-secure-password

# API Keys
GEMINI_API_KEY=your-gemini-api-key

# Frontend URL (voor CORS)
FRONTEND_URL=http://localhost:9696
```

> **Belangrijk**: Pas `election-backend/src/main/resources/application.properties` aan om deze environment variabelen te gebruiken!

**Stap 3: Start de applicatie**
```bash
docker-compose up --build -d
```

**Stap 4: Verifieer**
- Backend: http://localhost:8080
- Frontend: http://localhost:9696
- Swagger UI: http://localhost:8080/swagger-ui/index.html

---

### Optie 2: Handmatig Runnen (Development)

#### Backend Starten

```bash
cd election-backend

# Met Maven wrapper (aanbevolen)
./mvnw spring-boot:run

# Of met geïnstalleerde Maven
mvn spring-boot:run
```

Backend draait nu op: **http://localhost:8080**

#### Frontend Starten

```bash
cd election-frontend

# Installeer dependencies
npm install

# Start development server
npm run dev
```

Frontend draait nu op: **http://localhost:5173**

---

### Verplichte Environment Variabelen

Deze variabelen **MOETEN** geconfigureerd worden in `application.properties` of als environment variabelen:

| Variabele | Beschrijving | Voorbeeld |
|-----------|--------------|-----------|
| `spring.datasource.url` | PostgreSQL database URL | `jdbc:postgresql://host:5432/postgres` |
| `spring.datasource.username` | Database gebruikersnaam | `postgres` |
| `spring.datasource.password` | Database wachtwoord | `your-password` |
| `gemini.api.key` | Google Gemini API key (voor AI moderation) | `AIza...` |

**Tip**: Gebruik environment variabelen in plaats van hardcoded values:
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
gemini.api.key=${GEMINI_API_KEY}
```

---

## API Documentatie (Swagger UI)

De backend API is volledig gedocumenteerd met **Swagger UI** (SpringDoc OpenAPI).

### Toegang tot Swagger UI

1. **Start de backend** (zie Quick Start hierboven)

2. **Open in je browser**:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

3. **Voor Docker deployments**:
   ```
   http://localhost:18080/swagger-ui/index.html
   ```

### Wat vind je in Swagger UI?

Alle API endpoints zijn gedocumenteerd, gegroepeerd per controller:

- **Auth Controller** (`/api/auth/*`): Login, registratie, refresh tokens, logout
- **Poll Controller** (`/api/polls/*`): Polls aanmaken, stemmen, resultaten ophalen
- **Election Controller** (`/api/elections/*`): Verkiezingsdata ophalen (jaren, partijen, resultaten)
- **Admin Moderation Controller** (`/api/admin/moderation/*`): Content moderation en review
- **Municipality Controller** (`/api/municipalities/*`): Gemeentegegevens en resultaten
- **Party Controller** (`/api/parties/*`): Partijinformatie en stemresultaten

### API Endpoints Testen

1. Voor protected endpoints heb je een JWT token nodig
2. Test eerst `POST /api/auth/login` met valide credentials
3. Klik op "Authorize" bovenaan en plak het access token
4. Probeer endpoints uit via "Try it out"

### OpenAPI Specificatie (JSON)

De volledige OpenAPI specificatie is beschikbaar in JSON formaat:
```
http://localhost:8080/v3/api-docs
```

Importeer deze in tools zoals Postman, Insomnia, of andere API clients.

**[Meer details over Swagger configuratie in de documentatie ](docs/README.md#api-documentation)**

---

## Deployment Opties

### Docker Compose (Productie & Staging)

Voor productie deployment, zie de [volledige deployment guide](docs/deployment.md#docker-compose-aanbevolen).

**Samenvatting**:
```bash
docker-compose up --build -d
```

### Handmatige Deployment

Voor handmatige deployment op een server:

**Backend:**
```bash
cd election-backend
./mvnw clean package -DskipTests
java -jar target/election-backend-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd election-frontend
npm ci
npm run build
# Deploy dist/ folder naar Nginx/Apache
```

### GitLab CI/CD

De applicatie heeft GitLab CI/CD configuratie voor automatische deployment van documentatie (MkDocs).

Voor automatische application deployment, zie de [CI/CD sectie in deployment docs](docs/deployment.md#gitlab-cicd).

**[Volledige deployment handleiding ](docs/deployment.md)**

---

## Project Structuur

```
wiiroogaatee12/
├── election-backend/           # Spring Boot backend
│   ├── src/main/java/nl/hva/election_backend/
│   │   ├── controller/        # REST API controllers
│   │   ├── service/           # Business logic
│   │   ├── repository/        # Data access layer
│   │   ├── entity/            # JPA entities
│   │   └── dto/               # Data transfer objects
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── election-frontend/         # Vue 3 frontend
│   ├── src/
│   │   ├── views/            # Page components
│   │   ├── components/       # Reusable components
│   │   ├── services/         # API service layer
│   │   ├── store/            # Pinia stores
│   │   ├── router/           # Vue Router
│   │   └── apiClient.ts      # Custom fetch wrapper
│   ├── package.json
│   └── vite.config.ts
│
├── docs/                      # Documentatie
│   ├── architecture.md       # Architectuur + Class Diagrams
│   ├── tech-stack.md         # Technologies & Libraries
│   ├── design-decisions.md   # Design rationale
│   └── deployment.md         # Deployment guide
│
├── docker-compose.yml         # Docker Compose configuratie
├── .env                       # Environment variabelen (niet in git)
├── mkdocs.yml                # MkDocs configuratie
└── README.md                 # Dit bestand
```

---

## Security & Maintenance

### Authenticatie

De applicatie gebruikt **JWT (JSON Web Tokens)** met refresh token rotation:

- **Access tokens**: Korte levensduur (15-60 min), in HTTP-only cookies
- **Refresh tokens**: Langere levensduur (7 dagen), opgeslagen in database
- **Password hashing**: BCrypt met adaptive work factor

**Authenticatie flow:**
```
1. Login  Krijg access_token + refresh_token
2. API call  Verstuur access_token in Cookie header
3. Token expired  Auto-refresh via refresh_token
4. Logout  Invalideer refresh_token in database
```

### Content Moderation

Automatische content moderation via **Google Gemini API**:
- Alle user-generated content wordt gescand
- Ongepaste inhoud wordt geflagged voor admin review
- Admin panel voor manual review en approval

### Database Maintenance

- **Connection pooling**: HikariCP (max 4 connections)
- **Schema updates**: Hibernate `ddl-auto=validate` voor productie
- **Backups**: Implementeer reguliere database backups (aanbevolen: dagelijks)

**[Meer over security decisions ](docs/design-decisions.md#security-design-decisions)**

---

## Database Schema

De applicatie gebruikt **PostgreSQL** (via Supabase) met de volgende hoofdtabellen:

### Core Entities
- `users`: Gebruikers en authenticatie
- `elections`: Verkiezingsjaren en metadata
- `parties`: Politieke partijen per verkiezing
- `municipalities`: Gemeenten
- `constituencies`: Kieskringen
- `municipality_results`: Stemresultaten per gemeente

### Community Features
- `discussions`: User-created discussies
- `reactions`: Reacties op discussies
- `polls`: Polls met voting

### Voting Guide
- `statements`: Stemwijzer stellingen
- `voting_guide_parties`: Partijen in stemwijzer
- `voting_guide_answers`: Partij antwoorden
- `voting_guide_results`: User resultaten

**Complete ERD en class diagrams**: Zie [Architecture docs](docs/architecture.md#database-schema-erd)

---

## Testing

### Backend Testing

Unit tests zijn beschikbaar voor controllers en services:

```bash
cd election-backend
./mvnw test
```

**Test strategie:**
- **Controllers**: Tested via `MockMvc` (Spring Test)
- **Services**: Unit tests met Mockito
- **Integration tests**: Voor end-to-end flows

### Frontend Testing

```bash
cd election-frontend
npm run test  # (indien geconfigureerd)
```

**[Meer over testing strategy ](docs/design-decisions.md#testing--quality-assurance)**

---

## Troubleshooting

### Veelvoorkomende Problemen

#### Backend start niet op
- Controleer database connectiviteit: `pg_isready -h <host> -U <username>`
- Controleer environment variabelen
- Bekijk logs: `docker-compose logs backend`

#### Frontend laadt maar API calls falen
- Controleer of backend draait op poort 8080
- Controleer CORS configuratie in backend
- Pas `src/apiClient.ts` aan voor productie URL

#### Database migratie fouten
- Controleer `spring.jpa.hibernate.ddl-auto` setting
- Vergelijk database schema met entities

**[Volledige troubleshooting guide ](docs/deployment.md#troubleshooting)**

---

## Ondersteuning & Contact

Voor vragen over het project:
1. **Documentatie**: Raadpleeg eerst de documentatie in de `docs/` folder
2. **API vragen**: Gebruik Swagger UI voor endpoint details
3. **Deployment issues**: Zie [deployment.md](docs/deployment.md)
4. **Development team**: Neem contact op voor complexe vragen

---

## Documentatie Navigatie

### Voor Nieuwe Ontwikkelaars (Onboarding)
1. Lees [Tech Stack](docs/tech-stack.md) voor overzicht van technologieën
2. Lees [Architecture](docs/architecture.md) voor systeemoverzicht
3. Lees [Design Decisions](docs/design-decisions.md) voor context achter keuzes
4. Volg [Quick Start](#-quick-start---installatie--setup) om de app lokaal te draaien

### Voor Deployment
1. Lees [Deployment Guide](docs/deployment.md)
2. Configureer environment variabelen
3. Volg Docker Compose setup
4. Implementeer monitoring en backups

### Voor Maintenance
1. Bekijk [Architecture](docs/architecture.md) voor systeemoverzicht
2. Bekijk [Tech Stack](docs/tech-stack.md) voor dependency updates
3. Gebruik [Swagger UI](#-api-documentatie-swagger-ui) voor API testing
4. Raadpleeg [Troubleshooting](#-troubleshooting) bij problemen

### Voor Code Review
1. [Design Decisions](docs/design-decisions.md) voor rationale
2. [Tech Stack](docs/tech-stack.md) voor library keuzes
3. [Architecture](docs/architecture.md) voor patterns en structuur

---

## Productie Checklist

Voordat je naar productie gaat:

- [ ] Database credentials niet hardcoded
- [ ] API keys in environment variabelen
- [ ] HTTPS ingeschakeld
- [ ] CORS correct geconfigureerd voor productie domain
- [ ] JVM heap size ingesteld (`-Xmx`)
- [ ] Database connection pool geconfigureerd
- [ ] Health checks ingesteld
- [ ] Logging naar centrale locatie
- [ ] Database backup strategie geïmplementeerd
- [ ] Frontend `localhost` URLs vervangen door productie URLs

**[Volledige productie checklist ](docs/deployment.md#productie-checklist)**

---

## Licentie & Credits

**Project**: Wiiroogaatee12 - Dutch Election Platform  
**Ontwikkeld voor**: HBO-ICT, HvA  
**Technologie**: Spring Boot 3.5.5, Vue 3, PostgreSQL, Docker

---

## Quick Links

| Document | Beschrijving | Link |
|----------|--------------|------|
| **Architecture** | Volledige architectuur + class diagrams | [architecture.md](docs/architecture.md) |
| **Tech Stack** | Alle technologieën en libraries | [tech-stack.md](docs/tech-stack.md) |
| **Design Decisions** | Waarom bepaalde keuzes gemaakt zijn | [design-decisions.md](docs/design-decisions.md) |
| **Deployment** | Complete deployment handleiding | [deployment.md](docs/deployment.md) |
| **Swagger UI** | API documentatie (live) | http://localhost:8080/swagger-ui/index.html |

---
