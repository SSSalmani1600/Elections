# Architectuur Documentatie

## Inleiding

Dit document beschrijft de volledige architectuur van de Elections-applicatie. De applicatie is een fullstack web-applicatie voor het analyseren en interactief werken met Nederlandse verkiezingsgegevens.

---

## Architectuur Overzicht

De applicatie volgt een **klassieke 3-tier architectuur** met een duidelijke scheiding tussen frontend, backend en database.

```mermaid
graph TB
    subgraph "Frontend (Vue 3 + TypeScript)"
        A[Browser] --> B[Vue Router]
        B --> C[Views/Pages]
        C --> D[Components]
        D --> E[Services Layer]
        E --> F[API Client]
        C --> G[Pinia Store]
    end
    
    subgraph "Backend (Spring Boot)"
        H[REST Controllers] --> I[Service Layer]
        I --> J[Repository Layer]
        J --> K[JPA Entities]
    end
    
    subgraph "Database"
        L[(PostgreSQL/Supabase)]
    end
    
    F -->|HTTP/REST| H
    K --> L
```

---

## High-Level Systeem Diagram

```mermaid
flowchart LR
    subgraph Client["Client Layer"]
        Browser["🌐 Browser"]
    end
    
    subgraph Frontend["Frontend (Port 5173/9696)"]
        Vue["Vue 3 App"]
        Router["Vue Router"]
        Store["Pinia Store"]
        Services["Services"]
    end
    
    subgraph Backend["Backend (Port 8080)"]
        Controllers["REST API Controllers"]
        BusinessLogic["Business Logic Services"]
        DataAccess["Repository Layer"]
    end
    
    subgraph Data["Data Layer"]
        DB[(PostgreSQL)]
        External["🌍 External APIs"]
    end
    
    Browser --> Vue
    Vue --> Router
    Vue --> Store
    Vue --> Services
    Services -->|REST API| Controllers
    Controllers --> BusinessLogic
    BusinessLogic --> DataAccess
    DataAccess --> DB
    BusinessLogic --> External
    
    External -.->|Kiesraad| BusinessLogic
    External -.->|Google Gemini| BusinessLogic
```

---

## Backend Architectuur

### Layered Architecture Pattern

De backend volgt het **Layered Architecture Pattern** met vier duidelijk gescheiden lagen:

```mermaid
graph TB
    subgraph "Presentation Layer"
        C1[AuthController]
        C2[ElectionController]
        C3[PollController]
        C4[ModerationController]
        C5[AdminController]
        C6["... 15+ Controllers"]
    end
    
    subgraph "Business Logic Layer"
        S1[AuthService]
        S2[ElectionService]
        S3[JwtService]
        S4[PollService]
        S5[ModerationService]
        S6["... 19 Services"]
    end
    
    subgraph "Data Access Layer"
        R1[UserRepository]
        R2[ElectionRepository]
        R3[PollRepository]
        R4[ReactionRepository]
        R5["... 18 Repositories"]
    end
    
    subgraph "Domain Layer"
        E1[UserEntity]
        E2[ElectionEntity]
        E3[PartyEntity]
        E4[DiscussionEntity]
        E5["... 14 Entities"]
    end
    
    C1 --> S1
    C2 --> S2
    C3 --> S4
    C4 --> S5
    
    S1 --> R1
    S2 --> R2
    S4 --> R3
    S5 --> R4
    
    R1 --> E1
    R2 --> E2
    R3 --> E4
```

### Package Structuur

```
nl.hva.election_backend/
├── ElectionBackendApplication.java      # Spring Boot entry point
├── api/
│   └── DebugController.java             # Debug endpoints
├── controller/                          # REST Controllers
│   ├── AuthController.java
│   ├── AdminController.java
│   ├── AdminModerationController.java
│   ├── AdminPollController.java
│   ├── ElectionController.java
│   ├── ModerationController.java
│   ├── MunicipalityController.java
│   ├── PollController.java
│   ├── StatementController.java
│   ├── UserController.java
│   ├── VotingGuideAnswersController.java
│   ├── VotingGuidePartyController.java
│   ├── VotingGuideResultsController.java
│   └── parser/                          # XML Parsing Controllers
│       ├── CandidateController.java
│       ├── ConstituencyController.java
│       ├── DiscussionController.java
│       ├── ParserElectionController.java
│       └── PartyController.java
├── service/                             # Business Logic
│   ├── AdminService.java
│   ├── AuthService.java
│   ├── ConstituencyService.java
│   ├── DiscussionService.java
│   ├── DutchElectionService.java
│   ├── ElectionService.java
│   ├── JwtService.java
│   ├── ModerationLogService.java
│   ├── ModerationService.java
│   ├── MunicipalityService.java
│   ├── PartyService.java
│   ├── PartyViewpointService.java
│   ├── PollService.java
│   ├── ProfanityService.java
│   ├── ReactionService.java
│   ├── StatementService.java
│   ├── VotingGuideAnswersService.java
│   ├── VotingGuidePartyService.java
│   └── VotingGuideResultsService.java
├── repository/                          # Data Access
│   ├── ConstituencyRepository.java
│   ├── ConstituencyResultRepository.java
│   ├── DiscussionRepository.java
│   ├── ElectionRepository.java
│   ├── MunicipalityRepository.java
│   ├── MunicipalityResultRepository.java
│   ├── PartyRepository.java
│   ├── PartyViewpointRepository.java
│   ├── PollRepository.java
│   ├── PollVoteRepository.java
│   ├── ReactionRepository.java
│   ├── RefreshTokenRepository.java
│   ├── StatementRepository.java
│   ├── UserRepository.java
│   ├── VotingGuideAnswerRepository.java
│   ├── VotingGuidePartyRepository.java
│   └── VotingGuideResultsRepository.java
├── entity/                              # JPA Entities
│   ├── ConstituencyEntity.java
│   ├── ConstituencyResultEntity.java
│   ├── DiscussionEntity.java
│   ├── ElectionEntity.java
│   ├── MunicipalityEntity.java
│   ├── MunicipalityResultEntity.java
│   ├── PartyEntity.java
│   ├── PartyViewpointEntity.java
│   ├── ReactionEntity.java
│   ├── StatementEntity.java
│   ├── UserEntity.java
│   ├── VotingGuideAnswerEntity.java
│   ├── VotingGuidePartyEntity.java
│   ├── VotingGuideResultEntity.java
│   └── id/                              # Composite Key Classes
│       ├── PartyId.java
│       ├── ConstituencyId.java
│       └── MunicipalityResultId.java
└── dto/                                 # Data Transfer Objects
    ├── AuthenticationResponse.java
    ├── DiscussionDetailDto.java
    ├── DiscussionListItemDto.java
    ├── LoginRequest.java
    ├── LoginResponse.java
    ├── ModerationResponse.java
    ├── ModerationResult.java
    ├── MunicipalityDto.java
    ├── PartyResultDto.java
    ├── PollOverviewDto.java
    ├── PollResult.java
    ├── ReactionDto.java
    ├── RegisterRequest.java
    ├── RegisterResponse.java
    ├── TextRequest.java
    ├── TokenRefreshResponse.java
    ├── UpdateUserRequest.java
    └── VotingGuide*Dto.java
```

---

## Entity Class Diagrams

### Core Domain Model

```mermaid
classDiagram
    class UserEntity {
        -Long id
        -String username
        -String passwordHash
        -String email
        -Boolean isAdmin
        +getters/setters()
    }
    
    class DiscussionEntity {
        -Long id
        -String title
        -String body
        -String category
        -Long userId
        -Instant createdAt
        -Instant lastActivityAt
        -int reactionsCount
        +getters/setters()
    }
    
    class ReactionEntity {
        -Long id
        -Long userId
        -String message
        -String moderationStatus
        -String flaggedReason
        -Instant createdAt
        +getters/setters()
    }
    
    UserEntity "1" --> "*" DiscussionEntity : creates
    UserEntity "1" --> "*" ReactionEntity : writes
    DiscussionEntity "1" --> "*" ReactionEntity : contains
```

### Election Domain Model

```mermaid
classDiagram
    class ElectionEntity {
        -int year [PK]
        -String name
        -String election_code
        -LocalDate date
        +getters/setters()
    }
    
    class PartyEntity {
        -int year [PK]
        -String partyId [PK]
        -String name
        +getters/setters()
    }
    
    class ConstituencyEntity {
        -String constituencyId [PK]
        -int year [PK]
        -String name
        +getters/setters()
    }
    
    class MunicipalityEntity {
        -String municipalityId [PK]
        -int year [PK]
        -String name
        +getters/setters()
    }
    
    class MunicipalityResultEntity {
        -int year [PK]
        -String municipalityId [PK]
        -String partyId [PK]
        -int validVotes
        +getters/setters()
    }
    
    class ConstituencyResultEntity {
        -String constituencyId [PK]
        -int year [PK]
        -String partyId [PK]
        -int validVotes
        +getters/setters()
    }
    
    ElectionEntity "1" --> "*" PartyEntity : has parties
    ElectionEntity "1" --> "*" ConstituencyEntity : has constituencies
    ElectionEntity "1" --> "*" MunicipalityEntity : has municipalities
    
    MunicipalityEntity "1" --> "*" MunicipalityResultEntity : has results
    PartyEntity "1" --> "*" MunicipalityResultEntity : receives votes
    
    ConstituencyEntity "1" --> "*" ConstituencyResultEntity : has results
    PartyEntity "1" --> "*" ConstituencyResultEntity : receives votes
```

### Voting Guide Domain Model

```mermaid
classDiagram
    class StatementEntity {
        -Long id
        -String statement
        -String category
        -String explanation
        +getters/setters()
    }
    
    class VotingGuidePartyEntity {
        -Long id
        -String partyName
        +getters/setters()
    }
    
    class VotingGuideAnswerEntity {
        -Long id
        -Long statementId
        -Long partyId
        -String answer
        +getters/setters()
    }
    
    class VotingGuideResultEntity {
        -Long id
        -Date createdAt
        -Long userId
        -Long partyId
        -String partyName
        -Long percentage
    }
    
    StatementEntity "1" --> "*" VotingGuideAnswerEntity : has answers
    VotingGuidePartyEntity "1" --> "*" VotingGuideAnswerEntity : gives answers
    UserEntity "1" --> "*" VotingGuideResultEntity : has results
```

---

## Database Schema (ERD)

```mermaid
erDiagram
    USERS {
        bigint id PK
        varchar username
        varchar password_hash
        varchar email
        boolean is_admin
    }
    
    ELECTIONS {
        int year PK
        varchar name
        varchar election_code
        date date
    }
    
    PARTIES {
        int year PK,FK
        varchar party_id PK
        varchar name
    }
    
    CONSTITUENCIES {
        varchar constituency_id PK
        int year PK,FK
        varchar name
    }
    
    MUNICIPALITIES {
        varchar municipality_id PK
        int year PK,FK
        varchar name
    }
    
    MUNICIPALITY_RESULTS {
        int year PK,FK
        varchar municipality_id PK,FK
        varchar party_id PK,FK
        int valid_votes
    }
    
    DISCUSSIONS {
        bigint id PK
        varchar title
        text body
        varchar category
        bigint user_id FK
        timestamptz created_at
        timestamptz last_activity_at
        int reactions_count
    }
    
    REACTIONS {
        bigint id PK
        bigint discussion_id FK
        bigint user_id FK
        text message
        varchar moderation_status
        varchar flagged_reason
        timestamptz created_at
    }
    
    STATEMENTS {
        bigint id PK
        varchar statement
        varchar category
        varchar explanation
    }
    
    VOTING_GUIDE_RESULTS {
        bigint id PK
        timestamptz created_at
        bigint user_id FK
        bigint party_id FK
        varchar party_name
        bigint percentage
    }
    
    USERS ||--o{ DISCUSSIONS : creates
    USERS ||--o{ REACTIONS : writes
    DISCUSSIONS ||--o{ REACTIONS : contains
    USERS ||--o{ VOTING_GUIDE_RESULTS : has
    
    ELECTIONS ||--o{ PARTIES : has
    ELECTIONS ||--o{ CONSTITUENCIES : has
    ELECTIONS ||--o{ MUNICIPALITIES : has
    
    MUNICIPALITIES ||--o{ MUNICIPALITY_RESULTS : has
    PARTIES ||--o{ MUNICIPALITY_RESULTS : receives
```

---

## Frontend Architectuur

### Component Hiërarchie

```mermaid
graph TB
    subgraph "App Root"
        App[App.vue]
    end
    
    subgraph "Layouts"
        MainLayout[MainLayout.vue]
    end
    
    subgraph "Views/Pages"
        Home[HomeView]
        Elections[ElectionsView]
        Parties[PartiesView]
        PartyDetail[PartyDetailView]
        Forum[DiscussionsView]
        DiscussionDetail[DiscussionDetailView]
        VotingGuide[VotingGuideView]
        Account[AccountView]
        Admin[AdminDashboardView]
    end
    
    subgraph "Shared Components"
        Navbar[Navbar.vue]
        PartiesSlider[PartiesSlider.vue]
        ChartComponent[ChartComponent.vue]
        PollCard[PollCard.vue]
        ProgressBar[ProgressBar.vue]
    end
    
    subgraph "UI Components"
        Button[Button.vue]
        Card[Card.vue]
        Modal[Modal.vue]
        Input[Input.vue]
    end
    
    App --> MainLayout
    MainLayout --> Navbar
    MainLayout --> Home
    MainLayout --> Elections
    MainLayout --> Parties
    
    Home --> PartiesSlider
    Home --> PollCard
    Elections --> ChartComponent
    PartyDetail --> ChartComponent
```

### Frontend Package Structuur

```
election-frontend/src/
├── App.vue                              # Root component
├── main.ts                              # Entry point
├── apiClient.ts                         # Custom fetch wrapper met JWT refresh
├── router/
│   └── index.ts                         # Vue Router configuratie
├── store/
│   └── authStore.ts                     # Authenticatie state (Pinia-like)
├── services/                            # API Service Layer
│   ├── AdminService.ts
│   ├── AuthService.ts
│   ├── ElectionService.ts
│   ├── ModerationService.ts
│   ├── PartyService.ts
│   ├── PollService.ts
│   ├── StatementService.ts
│   ├── UserService.ts
│   ├── VotingGuideAnswersService.ts
│   ├── VotingGuidePartiesService.ts
│   ├── VotingGuideResultsService.ts
│   └── WikipediaService.ts
├── views/                               # Page Components
│   ├── HomeView.vue
│   ├── LoginView.vue
│   ├── RegisterView.vue
│   ├── ElectionsView.vue
│   ├── PartiesView.vue
│   ├── PartyDetailView.vue
│   ├── DiscussionsView.vue
│   ├── DiscussionDetailView.vue
│   ├── VotingGuideHomeView.vue
│   ├── VotingGuideView.vue
│   ├── VotingGuideResultsView.vue
│   ├── ElectionCalenderView.vue
│   ├── AccountView.vue
│   └── admin/
│       ├── AdminDashboardView.vue
│       ├── AdminModerationView.vue
│       └── AdminPollsView.vue
├── components/
│   ├── Navbar.vue
│   ├── PartiesSlider.vue
│   ├── ChartComponent.vue
│   ├── PollCard.vue
│   ├── ProgressBar.vue
│   ├── AdminMenu.vue
│   ├── CustomToolTip.vue
│   ├── icons/                           # Icon components
│   ├── maps/                            # Map components
│   └── ui/                              # Reusable UI primitives
│       ├── Button.vue
│       ├── Card.vue
│       ├── Input.vue
│       └── ... (27 components)
├── layouts/
│   └── MainLayout.vue
├── types/
│   └── api.ts                           # TypeScript type definities
├── lib/
│   └── utils.ts                         # Utility functions
└── assets/
    └── ... (CSS, images)
```

### State Management Flow

```mermaid
sequenceDiagram
    participant User
    participant Component
    participant AuthStore
    participant Service
    participant API
    participant Backend
    
    User->>Component: Login action
    Component->>AuthStore: login(email, password)
    AuthStore->>Service: loginRequest()
    Service->>API: apiFetch('/api/auth/login')
    API->>Backend: POST /api/auth/login
    Backend-->>API: JWT + User data
    API-->>Service: Response
    Service-->>AuthStore: User object
    AuthStore->>AuthStore: state.user = user
    AuthStore-->>Component: Success
    Component-->>User: Redirect to home
```

---

## Authenticatie Architectuur

### JWT Token Flow

```mermaid
sequenceDiagram
    participant Browser
    participant Frontend
    participant Backend
    participant Database
    
    Note over Browser,Database: Login Flow
    Browser->>Frontend: Submit credentials
    Frontend->>Backend: POST /api/auth/login
    Backend->>Database: Validate user
    Database-->>Backend: User found
    Backend->>Backend: Generate JWT + Refresh token
    Backend->>Database: Store refresh token
    Backend-->>Frontend: Set-Cookie: jwt, refresh_token
    Frontend-->>Browser: Redirect to dashboard
    
    Note over Browser,Database: Authenticated Request
    Browser->>Frontend: Request protected resource
    Frontend->>Backend: GET /api/protected (Cookie: jwt)
    Backend->>Backend: Validate JWT
    Backend-->>Frontend: Protected data
    
    Note over Browser,Database: Token Refresh Flow
    Browser->>Frontend: Request (expired JWT)
    Frontend->>Backend: GET /api/protected
    Backend-->>Frontend: 401 Unauthorized
    Frontend->>Backend: POST /api/auth/refresh
    Backend->>Database: Validate refresh token
    Backend->>Backend: Generate new JWT
    Backend-->>Frontend: New JWT cookie
    Frontend->>Backend: Retry original request
    Backend-->>Frontend: Protected data
```

---

## Data Flow Architectuur

### Verkiezingsdata Import Flow

```mermaid
flowchart TD
    A[Kiesraad Website] -->|HTML Scraping| B[DutchElectionService]
    B -->|XML URLs| C[XML Parser]
    C --> D[DutchDefinitionTransformer]
    C --> E[DutchCandidateTransformer]
    C --> F[DutchResultTransformer]
    C --> G[DutchNationalVotesTransformer]
    C --> H[DutchConstituencyVotesTransformer]
    C --> I[DutchMunicipalityVotesTransformer]
    
    D --> J[(Elections Table)]
    D --> K[(Parties Table)]
    D --> L[(Municipalities Table)]
    E --> M[(Candidates Table)]
    F --> N[(Results Tables)]
    G --> N
    H --> N
    I --> N
```

### Content Moderation Flow

```mermaid
flowchart LR
    A[User Submit] --> B[ReactionController]
    B --> C[ReactionService]
    C --> D{AI Moderation}
    D -->|Call| E[Google Gemini API]
    E -->|Response| D
    D -->|Safe| F[Save with PENDING]
    D -->|Flagged| G[Save with FLAGGED]
    F --> H[(Database)]
    G --> H
    H --> I[Admin Review Queue]
    I -->|Approve| J[Status: APPROVED]
    I -->|Reject| K[Status: REJECTED]
```

---

## API Endpoints Overzicht

### Authentication Endpoints
| Method | Endpoint | Beschrijving |
|--------|----------|--------------|
| POST | `/api/auth/login` | Gebruiker inloggen |
| POST | `/api/auth/register` | Nieuwe gebruiker registreren |
| POST | `/api/auth/refresh` | Access token vernieuwen |
| DELETE | `/api/auth/logout` | Uitloggen |
| GET | `/api/auth/session` | Huidige sessie ophalen |

### Election Data Endpoints
| Method | Endpoint | Beschrijving |
|--------|----------|--------------|
| GET | `/api/elections` | Alle verkiezingen |
| GET | `/api/elections/{year}` | Specifieke verkiezing |
| GET | `/api/parties` | Alle partijen |
| GET | `/api/parties/{id}` | Specifieke partij |
| GET | `/api/municipalities` | Alle gemeenten |
| GET | `/api/constituencies` | Alle kieskringen |

### Discussion/Forum Endpoints
| Method | Endpoint | Beschrijving |
|--------|----------|--------------|
| GET | `/api/discussions` | Alle discussies |
| GET | `/api/discussions/{id}` | Specifieke discussie |
| POST | `/api/discussions` | Nieuwe discussie |
| POST | `/api/discussions/{id}/reactions` | Reactie toevoegen |

### Voting Guide Endpoints
| Method | Endpoint | Beschrijving |
|--------|----------|--------------|
| GET | `/api/statements` | Alle stemwijzer stellingen |
| GET | `/api/voting-guide/parties` | Partijen voor stemwijzer |
| POST | `/api/voting-guide/results` | Stemwijzer resultaat opslaan |
| GET | `/api/voting-guide/results/{userId}` | Resultaten ophalen |

### Admin Endpoints
| Method | Endpoint | Beschrijving |
|--------|----------|--------------|
| GET | `/api/admin/moderation/pending` | Pending moderaties |
| POST | `/api/admin/moderation/{id}/approve` | Reactie goedkeuren |
| POST | `/api/admin/moderation/{id}/reject` | Reactie afwijzen |
| POST | `/api/admin/polls` | Nieuwe poll aanmaken |

---

## Deployment Architectuur

```mermaid
graph TB
    subgraph "Production Environment"
        subgraph "Docker Compose"
            FE["Frontend Container<br/>Port 9696"]
            BE["Backend Container<br/>Port 8080"]
        end
        
        subgraph "External Services"
            DB["Supabase PostgreSQL<br/>(Cloud)"]
            GEMINI["Google Gemini API"]
        end
    end
    
    subgraph "CI/CD"
        GL["GitLab CI/CD"]
        MKDOCS["MkDocs Documentation"]
    end
    
    FE --> BE
    BE --> DB
    BE --> GEMINI
    GL --> FE
    GL --> BE
    GL --> MKDOCS
```

---

## Componenten Samenvatting

| Component | Technologie | Verantwoordelijkheid |
|-----------|-------------|----------------------|
| **Frontend** | Vue 3, TypeScript, Vite | User interface, routing, state management |
| **Backend** | Spring Boot 3.5.5, Java 21 | REST API, business logic, data access |
| **Database** | PostgreSQL (Supabase) | Persistent data storage |
| **Auth** | JWT + BCrypt | Stateless authentication |
| **AI Moderation** | Google Gemini | Content filtering |
| **Styling** | TailwindCSS, PrimeVue | UI components en design |
| **Maps** | Leaflet | Geografische visualisaties |
| **Charts** | Chart.js, Unovis | Data visualisaties |

---

## Key Design Patterns Gebruikt

1. **Layered Architecture** - Backend scheiding in Controller/Service/Repository/Entity
2. **Repository Pattern** - Data access abstractie via Spring Data JPA
3. **DTO Pattern** - Entities gescheiden van API responses
4. **Composite Key Pattern** - Election data met samengestelde sleutels
5. **Composable Pattern** - Vue 3 Composition API voor herbruikbare logica
6. **Service Layer Pattern** - Frontend services voor API communicatie
7. **Route Guards** - Vue Router authenticatie checks
8. **Token Refresh Pattern** - Automatische JWT verversing

---

> **Zie ook:**
> - [Design Decisions](design-decisions.md) - Gedetailleerde uitleg van alle architectuurbeslissingen
> - [Tech Stack](tech-stack.md) - Overzicht van alle gebruikte technologieën
> - [Deployment](deployment.md) - Deployment instructies
