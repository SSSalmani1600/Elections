# Technology Stack & Libraries Documentation

## Overzicht

Dit project is een **fullstack Java/TypeScript applicatie** voor analyseren en interactief werken met Nederlandse verkiezingsgegevens. Het maakt gebruik van moderne frameworks en libraries voor zowel backend als frontend.

---

## Backend Technologies

### Framework & Runtime

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **Spring Boot** | 3.5.5 | Web framework & dependency injection | Industry-standard Java framework met uitgebreide ecosysteem, goede documentatie, en snelle development |
| **Java** | 21 | Programming language | LTS versie met moderne features, performance, en lange support periode |
| **Spring Web** | 3.5.5 | REST API development | Built-in support voor controllers, request mapping, en HTTP handling |
| **Spring Data JPA** | 3.5.5 | ORM & database abstraction | Reduceert boilerplate code, ondersteunt complex data access patterns |

### Database & Persistence

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **PostgreSQL** | Latest | Relationele database | Robuuste, open-source, ondersteunt complexe queries en grote datasets |
| **Supabase** | - | Database hosting (cloud) | Drop-in PostgreSQL replacement, makkelijke setup, kosteloos tier |
| **HikariCP** | Built-in | Connection pooling | Snelle en betrouwbare database connection pool |
| **Hibernate** | Via Spring | SQL dialect & persistence | Standaard ORM in Spring ecosystem, PostgreSQL dialect voor optimalisatie |

**Configuratie in `application.properties`:**
```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres
spring.datasource.hikari.maximum-pool-size=4
spring.datasource.hikari.minimum-idle=1
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Security & Authentication

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **JJWT (JSON Web Token)** | 0.12.6 | JWT creation & validation | Stateless authentication, eenvoudig te implementeren, goed ondersteund |
| **jbcrypt** | 0.4 | Password hashing | Veilige password hashing met adaptive work factor |
| **Spring Security** (impliciet) | - | CORS & authentication filters | Framework-native security handling |

**JWT Implementatie:**
- Access tokens: Kort levensduur (ideaal in HTTP-only cookies)
- Refresh tokens: Langere levensduur (beveiligd in database)
- CORS configuratie voor localhost development en production deployment

### HTTP & Networking

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **OkHttp** | 4.12.0 | HTTP client | Robuuste HTTP client met connection pooling, request interceptors, en moderne API |

**Gebruik:** Fetchen van externe data (bijv. Kiesraad website, Google Gemini API)

### Parsing & Data Processing

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **JSoup** | 1.21.2 | HTML/XML parsing | Elegante API voor HTML/XML document parsing, gebruiker voor verkiezingsgegevens |

**Gebruik:**
- Parsen van XML-bestanden met verkiezingsresultaten
- Extractie van relevante data uit HTML-pagina's
- Web scraping voor externe verkiezingsbronnen

### Testing

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **JUnit 5** | Built-in | Unit testing framework | Modern testing framework met Spring Boot integration |
| **Mockito** | Built-in | Mocking framework | Eenvoudig mock objects creëren voor unit tests |
| **Spring Test** | Built-in | Integration testing | `MockMvc` voor controller testing zonder echte HTTP server |

**Test Coverage:**
- `AdminModerationControllerTest`: Controller layer testing
- Integration tests voor services

### Build Management

| Technologie | Tool | Doel | Waarom gekozen? |
|---|---|---|---|
| **Maven** | mvnw (wrapper) | Dependency management & build | Standaard build tool in Java ecosystem, reproducible builds |
| **Maven Compiler Plugin** | - | Java compilation | Spring Boot parent pom bevat optimale compiler configuratie |

---

## Frontend Technologies

### Framework & Runtime

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **Vue 3** | 3.5.18 | Frontend framework | Reactief, composable, betere performance dan Vue 2, minder overhead dan React/Angular |
| **TypeScript** | ~5.8.0 | Type safety | Preventie van runtime errors, betere IDE support, refactoring tools |
| **Node.js** | 20.19+ / 22.12+ | JavaScript runtime | Modern async/await support, package management via npm |
| **Vite** | 7.0.6 | Build tool & dev server | Snelle development experience, optimized production builds, ES modules native |

### UI Components & Styling

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **PrimeVue** | 4.5.3 | UI component library | Uitgebreide component library met accessibility features |
| **PrimeIcons** | 7.0.0 | Icon library | Consistent icon set, geoptimaliseerd voor web |
| **TailwindCSS** | 4.1.17 | Utility-first CSS | Snelle styling, consistent design system, small bundle size |
| **Tailwind Merge** | 3.3.1 | Utility class merging | Voorkomt CSS class conflicts, belangrijk met dynamic classnames |
| **@tailwindcss/vite** | 4.1.13 | Vite integration | Native Vite integration voor Tailwind |
| **Reka UI** | 2.5.1 | Headless UI library | Accessible component primitives met custom styling |
| **Class Variance Authority** | 0.7.1 | Component styling | Variant-based styling system, reduceert boilerplate in components |

**Styling Approach:**
- Utility-first CSS met TailwindCSS
- Component-level styling met class-variance-authority
- Twee-laags styling: utiliteiten + component varianten

### Data Visualization

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **Chart.js** | 4.5.1 | Charts & graphs | Eenvoudig, responsive, ondersteunt veel chart types |
| **Leaflet** | 1.9.4 | Interactive maps | Lightweight mapping library, goed voor geo-data visualisatie |
| **Unovis (TS/Vue)** | 1.6.1 | Advanced visualization | Modern, high-performance visualization library |
| **Swiper** | 12.0.2 | Carousel/slider component | Touch-friendly sliders, veel configuratie opties |

**Visualisatie Cases:**
- Partijresultaten per gemeente (Leaflet kaarten)
- Stemmen over tijd (Chart.js)
- Interactieve grafieken (Unovis)

### HTTP & API Communication

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **Fetch API** | Native | HTTP requests | Modern standard, beter dan XMLHttpRequest, goed ondersteund |
| **Custom apiClient.ts** | - | Request wrapper | JWT refresh handling, automatic retry on 401 |

**Implementatie:**
```typescript
// JWT refresh flow
async function apiFetch(input: RequestInfo, init?: RequestInit, retry = true) {
  const res = await fetch(input, { ...init, credentials: 'include' })
  if (res.status === 401 && retry) {
    // Refresh token en retry
    return apiFetch(input, init, false)
  }
  return res
}
```

### State Management

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **Pinia** | 3.0.4 | Vue state management | Officieel Vue state management, replace van Vuex, intuïtief API |
| **Vue Router** | 4.5.1 | Client-side routing | Officieel Vue router, lazy loading support, navigation guards |

### Utilities & Helpers

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **@vueuse/core** | 13.9.0 | Vue composition utilities | Reusable composables (watchers, lifecycle hooks, etc.) |
| **@vueuse/integrations** | 14.0.0 | Third-party integrations | Composables voor populaire libraries |
| **async-validator** | 4.2.5 | Form validation | Schema-based form validation |
| **Fuse.js** | 7.1.0 | Fuzzy search | Client-side fuzzy search, snel en eenvoudig |
| **clsx** | 2.1.1 | Conditional className builder | Utility voor dynamic classnames |
| **Lucide Vue Next** | 0.546.0 | Icon library | Modern, tree-shakeable icons |
| **tw-animate-css** | 1.4.0 | Animations | Tailwind-compatible animations |

### Development Tools

| Technologie | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **ESLint** | 9.31.0 | Code linting | Detecteren van bugs en code style issues |
| **Prettier** | 3.6.2 | Code formatting | Consistent code formatting across team |
| **TypeScript ESLint** | Latest | TS-aware linting | Type-safe linting voor TypeScript |
| **Vue ESLint Plugin** | 10.3.0 | Vue-specific rules | Linting voor Vue templates |
| **Vite Plugin Vue DevTools** | 8.0.0 | Vue development | DevTools integration voor debugging |
| **npm-run-all2** | 8.0.4 | Script runner | Run multiple npm scripts in parallel |

**Build & Dev Commands:**
```json
{
  "dev": "vite",
  "build": "run-p type-check \"build-only {@}\" --",
  "type-check": "vue-tsc --build",
  "lint": "eslint . --fix",
  "format": "prettier --write src/"
}
```

---

## External APIs & Services

### Google Gemini API

| Service | Versie | Doel | Waarom gekozen? |
|---|---|---|---|
| **Google Gemini** | 2.5-flash-lite | AI text moderation & generation | Gratis tier voor moderation, snelle responses |

**Configuratie:**
```properties
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent
```

**Gebruik:**
- Text moderation (profanity, inappropriate content)
- Potentieel voor content generation in voting guide

### Kiesraad API

- **URL:** https://www.kiesraad.nl/actueel/activiteiten
- **Doel:** Fetchen van beschikbare verkiezingen
- **Format:** HTML parsing met JSoup

---

## Deployment & Infrastructure

### Docker

| Component | Image | Port | Doel |
|---|---|---|---|
| **Backend** | Custom (Dockerfile) | 8080 | Spring Boot applicatie |
| **Frontend** | Custom (Dockerfile) | 9696 | Vue 3 applicatie |

**Docker Compose:**
```yaml
services:
  backend:
    build: ./election-backend
    ports: ["8080:8080"]
  frontend:
    build: ./election-frontend
    ports: ["9696:80"]
    depends_on: [backend]
```

---

## Design Patterns & Architecture

### Backend Patterns

1. **Layered Architecture:**
   - Controllers (HTTP routing)
   - Services (business logic)
   - Repositories (data access)
   - Entities (database models)

2. **Dependency Injection:** Spring IoC container
3. **JWT + Refresh Token:** Stateless authentication
4. **Entity-DTO Pattern:** Data transfer objects voor external communication

### Frontend Patterns

1. **Component-Based:** Vue single-file components
2. **Composable-Based:** `@vueuse/core` voor reusable logic
3. **Service Layer:** TypeScript services voor API communication
4. **Route Guards:** Vue Router guards voor authentication
5. **Pinia Stores:** Centralized state management

---

## Performance Considerations

### Backend
- HikariCP connection pooling (max 4 connections)
- Database query optimization via JPA
- Request interceptors voor efficient HTTP communication

### Frontend
- Vite code splitting & tree shaking
- Lazy loading van routes
- Component memoization (Vue 3)
- Image optimization
- Tailwind CSS purging (only used classes included)

---

## Security Considerations

1. **Authentication:**
   - JWT tokens (short-lived)
   - Refresh tokens (long-lived, stored in DB)
   - HTTP-only cookies voor token storage

2. **Password Handling:**
   - bcrypt hashing
   - Adaptive work factor

3. **CORS:**
   - Whitelist van allowed origins
   - Credentials enabled voor cross-origin requests

4. **Content Moderation:**
   - Google Gemini API integration
   - Automatic flagging van inappropriate content

---

## Third-Party Dependencies Summary

### Backend Dependencies (11 total)
- Spring Boot Web, Data JPA, Test
- JWT handling (JJWT, jbcrypt)
- HTTP client (OkHttp)
- Data parsing (JSoup)
- PostgreSQL driver

### Frontend Dependencies (30+ total)
- Vue 3 & ecosystem (Router, Pinia)
- UI libraries (PrimeVue, Reka UI)
- Styling (TailwindCSS)
- Visualization (Chart.js, Leaflet, Unovis)
- Utilities (VueUse, async-validator, Fuse.js)

**Total installed size:** ~500MB (node_modules), ~100MB (Spring dependencies)

---

## Versioning & Compatibility

- **Node:** 20.19+ / 22.12+
- **Java:** 21 LTS
- **PostgreSQL:** 12+
- **Spring Boot:** 3.5.5 (Spring Framework 6.2.11)
- **Vue:** 3.5.18

---

## Update & Maintenance Guidelines

1. **Spring Boot updates:** Check Spring release notes, test thoroughly
2. **Vue updates:** Monitor Vue 3 changelog, test component compatibility
3. **Security updates:** Prioritize JJWT, Spring Security patches
4. **Dependency audits:** `npm audit`, Maven security plugin
5. **Breaking changes:** Always review dependency changelogs before updating
