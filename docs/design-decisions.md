# Design Decisions Documentation

## Inleiding

Dit document beschrijft de belangrijkste design choices en architecture beslissingen die in dit project zijn gemaakt. Waar wordt uitgelegd waarom bepaalde keuzes zijn gemaakt.

---

## Backend Architecture

### 1. Layered Architecture

**Beslissing:** Het backend volgt een klassieke gelaagde architectuur.

**Structuur:**
```
controllers/
├── AuthController.java
├── AdminController.java
├── PollController.java
└── ... (meer controllers)

service/
├── AuthService.java
├── JwtService.java
├── PollService.java
├── ElectionService.java
└── ... (meer services)

repository/
├── UserRepository.java
├── ElectionRepository.java
└── ... (meer repositories)

entity/
├── UserEntity.java
├── PartyEntity.java
├── ElectionEntity.java
└── ... (database entities)
```

**Voordelen:**
- Duidelijke scheiding van concerns
- Gemakkelijk testbaar (service layer kan gemockt worden)
- Onderhoudbaar en schaalbaar
- Standaard patroon in Spring Boot projecten

---

### 2. Entity-DTO Pattern

**Beslissing:** Persistentie entities en data transfer objects zijn gescheiden.

**Voorbeeld:**

```java
// Entity (Database model)
@Entity
public class UserEntity {
    @Id private UUID id;
    @Column(unique = true) private String username;
    private String passwordHash;
}

// DTO (API response)
public class UserDto {
    public String username;
    // Geen password field - nooit exposure naar client
}
```

**Voordelen:**
- Security: Password hashes nooit exposed aan frontend
- API versioning: Kunnen DTOs wijzigen zonder entity schema te breken
- Flexibility: Entities kunnen extra internal fields hebben
- Performance: Kunnen selectively laden van data

---

### 3. Dependency Injection via Spring IoC

**Beslissing:** Alle dependencies worden ge-inject door Spring container.

**Voorbeeld:**

```java
@Service
public class PollService {
    private final PollRepository pollRepository;
    private final JwtService jwtService;
    
    // Constructor injection (preferred)
    public PollService(PollRepository pollRepository, JwtService jwtService) {
        this.pollRepository = pollRepository;
        this.jwtService = jwtService;
    }
}
```

**Voordelen:**
- Loose coupling tussen classes
- Gemakkelijk testen (mock dependencies)
- Spring manages lifecycle automatisch
- No null pointer exceptions (Spring validates injectables)

**Constructor vs Field Injection:**
- Constructor injection gekozen (best practice)
- Field injection (@Autowired) vermeden (moeilijker testen)

---

### 4. JWT + Refresh Token Authentication

**Beslissing:** Stateless authentication met JWT tokens en refresh token rotation.

**Implementatie:**

```java
@Service
public class JwtService {
    public String generateAccessToken(User user) {
        // Korte levensduur (15-60 minuten)
        return Jwts.builder()
            .subject(user.getId().toString())
            .issuedAt(now)
            .expiration(now + 15 minutes)
            .signWith(key)
            .compact();
    }
    
    public String generateRefreshToken(User user) {
        // Langere levensduur (7-30 dagen)
        return Jwts.builder()
            .subject(user.getId().toString())
            .expiration(now + 7 days)
            .signWith(key)
            .compact();
    }
}
```

**Token Flow:**
```
1. Login: POST /api/auth/login
   → Returns: access_token (cookie) + refresh_token (HttpOnly cookie)

2. Request: GET /api/protected
   Header: Cookie: jwt=access_token
   → Server validates JWT

3. Access token expired: Server returns 401
   → Frontend calls POST /api/auth/refresh
   → Server validates refresh_token → Issues new access_token

4. Logout: DELETE /api/auth/logout
   → Invalidate refresh_token in database
```

**Voordelen:**
- Stateless (schaalbaar horizontaal)
- Security: Access token kort geldig
- Refresh token rotation: Refresh tokens in database (kunnen invalidated worden)
- HttpOnly cookies: XSS protection
- CORS credentials: Secure cross-origin requests

---

### 5. Composite Key voor Election Data

**Beslissing:** `PartyEntity` en `MunicipalityResultEntity` gebruiken composite keys.

```java
@Entity
@IdClass(PartyId.class)
public class PartyEntity {
    @Id private int year;           // Election year
    @Id @Column(name = "party_id") 
    private String partyId;         // Party identifier
    
    @Column(nullable = false)
    private String name;             // Party name
}
```

**Voordelen:**
- Constraints: Elk party is unique per year
- Historical data: Kunnen parties over years tracken
- No surrogate keys: Semantically meaningful
- Efficient queries: Direct lookup by (year, partyId)

---

### 6. XML Parsing met Transformers

**Beslissing:** XML parsing is gerefactored naar Transformer pattern.

```java
DutchElectionParser parser = new DutchElectionParser(
    new DutchDefinitionTransformer(election, partyService, municipalityService),
    new DutchCandidateTransformer(election),
    new DutchResultTransformer(election),
    new DutchNationalVotesTransformer(election),
    new DutchConstituencyVotesTransformer(election, constituencyService),
    new DutchMunicipalityVotesTransformer(election)
);
```

**Voordelen:**
- Single Responsibility Principle: Elke transformer een ding
- Testable: Kunnen transformers los testen
- Reusable: Transformers kunnen geshared worden
- Maintainable: Duidelijk wat elke transformer doet

---

### 7. CORS Configuration

**Beslissing:** Whitelist van allowed origins, credentials enabled.

```java
@Configuration
public class WebCorsConfig {
    config.setAllowedOrigins(List.of(
        "http://localhost:5173",      // Frontend dev
        "http://localhost:5174",      // Alternative dev port
        "http://localhost:3000",      // Other dev
        "http://oege.ie.hva.nl:9696"  // Production
    ));
    config.setAllowCredentials(true); // Cookies allowed
}
```

**Voordelen:**
- Security: Whitelist approach, niet wildcard (*)
- Credentials: Cookies werken in cross-origin requests
- Exposing headers: Set-Cookie, Authorization exposed

---

### 8. HikariCP Connection Pooling

**Beslissing:** HikariCP pool met beperkte connecties.

```properties
spring.datasource.hikari.maximum-pool-size=4
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.connection-timeout=30000
```

**Voordelen:**
- Performance: Reuse van connections
- Resource management: Beperkt aantal connections
- Auto-tuning: HikariCP optimaliseert automatisch
- Lightweight: Minder memory dan andere pooling libraries

**Pool size van 4:**
- Geschikt voor mid-tier application
- Kan adjusted worden op basis van load testing

---

## Frontend Architecture

### 1. Vue 3 Composition API

**Beslissing:** Vue 3 met Composition API (niet Options API).

**Voorbeeld:**

```typescript
// Composition API (modern, huidige keuze)
export function useCounterStore() {
    const count = ref(0)
    const increment = () => count.value++
    return { count, increment }
}

// Options API (legacy, niet gekozen)
export default {
    data() { return { count: 0 } },
    methods: { increment() { this.count++ } }
}
```

**Voordelen:**
- Reusability: Composables kunnen geshared worden
- Type safety: Beter TypeScript support
- Smaller bundle: Ongebruikte code kan shaken
- Better performance: Reactivity tracking optimized
- Modern: Vue 3 recommended approach

---

### 2. Pinia State Management

**Beslissing:** Pinia voor centralized state management.

```typescript
// Pinia store
export const useUserStore = defineStore('user', () => {
    const user = ref<User | null>(null)
    const isAuthenticated = computed(() => user.value !== null)
    
    async function login(credentials: LoginRequest) {
        const response = await api.post('/auth/login', credentials)
        user.value = response.data
    }
    
    return { user, isAuthenticated, login }
})
```

**Voordelen:**
- Official Vue state management
- Type-safe: Full TypeScript support
- Composition API style: Matches Vue 3
- DevTools: Vue DevTools integration
- No boilerplate: Minder code dan Vuex

---

### 3. Custom API Client met JWT Refresh

**Beslissing:** Custom `apiFetch` wrapper in plaats van axios/fetch bibliotheek.

```typescript
async function apiFetch(
    input: RequestInfo,
    init: RequestInit = {},
    retry = true,
): Promise<Response> {
    const res = await fetch(input, {
        ...init,
        credentials: 'include'  // Include cookies
    })
    
    // Auto-refresh on 401
    if (res.status === 401 && retry) {
        await refreshToken()
        return apiFetch(input, init, false)  // Retry once
    }
    
    return res
}
```

**Voordelen:**
- Minimaal: Geen extra dependency
- Custom logic: JWT refresh handling
- Type safe: Matches Fetch API types
- Lightweight: Niet meer dan noddig

---

### 4. TailwindCSS + Component Variants

**Beslissing:** Utility-first CSS met class-variance-authority.

```typescript
// Button component met variants
const buttonVariants = cva('px-4 py-2 rounded', {
    variants: {
        variant: {
            primary: 'bg-blue-600 text-white',
            secondary: 'bg-gray-200 text-gray-900'
        },
        size: {
            sm: 'text-sm',
            lg: 'text-lg'
        }
    }
})

// Usage
<button :class="buttonVariants({ variant: 'primary', size: 'lg' })">
    Click me
</button>
```

**Voordelen:**
- Consistency: Design system via variants
- Performance: CSS pruning (only used classes)
- DRY: Geen duplicate styles
- Type-safe: Variants zijn TypeScript type-checked
- Maintainable: Centralized button styling

---

### 5. Component Composition Strategy

**Beslissing:** Small, focused components met PrimeVue + custom components.

**Structure:**

```
components/
├── ui/                          // Reusable primitives
│   ├── Button.vue
│   ├── Card.vue
│   └── Modal.vue
├── forms/                       // Form-specific
│   ├── LoginForm.vue
│   └── PollForm.vue
├── features/                    // Feature-specific
│   ├── VotingGuide.vue
│   ├── PartiesSlider.vue
│   └── ChartComponent.vue
└── layouts/                     // Page layouts
    └── AdminLayout.vue
```

**Voordelen:**
- Reusability: `ui/` components across pages
- Maintainability: Clear folder structure
- Testing: Small components easier to test
- Performance: Code splitting per feature

---

### 6. Router Guards for Authentication

**Beslissing:** Route guards enforce authentication checks.

```typescript
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    
    if (to.meta.requiresAuth && !userStore.isAuthenticated) {
        next('/login')
    } else {
        next()
    }
})
```

**Voordelen:**
- Security: Protected routes
- UX: Prevent navigation attempts
- Centralized: Single place for auth logic

---

## Data Flow & API Design

### 1. Request/Response Envelope Pattern (NOT used)

**Beslissing:** Geen envelope pattern, direct responses.

```typescript
// Direct response (gekozen)
GET /api/parties
Response: PartyEntity[]

// vs Envelope (niet gekozen)
GET /api/parties
Response: {
    status: "success",
    data: PartyEntity[],
    message: null
}
```

**Voordelen van gekozen aanpak:**
- Simplicity: RESTful standard
- Performance: Minder nesting
- Tools: Direct OpenAPI/Swagger mapping

**Alternatieven:**
- Envelope pattern: Extra nesting, minder RESTful

---

### 2. Reactive Forms

**Beslissing:** Vue 3 `ref` voor form state.

```typescript
const pollQuestion = ref('')
const pollChoices = ref<string[]>([''])

async function submitPoll() {
    const poll = await pollService.createPoll({
        question: pollQuestion.value,
        choices: pollChoices.value
    })
}
```

**Voordelen:**
- Simplicity: Direct state binding
- Type-safe: TypeScript types
- Reactive: Auto-re-render on changes

---

## Data Structures & Algorithms

### 1. Set vs List voor Collections

**Beslissing:** Volgt Java conventions: HashSet voor parties, ArrayList voor votes.

```java
public class Election {
    private final List<Party> parties = new ArrayList<>();
    private Set<Constituency> constituencies = new HashSet<>();
}
```

**Voordelen:**
- Set voor constituencies: Unique check, O(1) lookup
- List voor parties: Order preservation, index access
- Standard: Java framework conventions

---

### 2. HashMap voor Vote Aggregation

**Beslissing:** HashMap voor stemmen per gemeente.

```java
public class Party {
    private final Map<String, Integer> votesByMunicipality = new HashMap<>();
    
    public int getVotes(String municipality) {
        return votesByMunicipality.getOrDefault(municipality, 0);
    }
}
```

**Voordelen:**
- Performance: O(1) lookup per gemeente
- Flexibility: Kan null values handelen
- Aggregation: Perfect voor data analysis

---

## Security Design Decisions

### 1. Password Hashing with bcrypt

**Beslissing:** BCrypt voor password hashing.

```java
private String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
}

public boolean verifyPassword(String plaintext, String hash) {
    return BCrypt.checkpw(plaintext, hash);
}
```

**Voordelen:**
- Adaptive: Work factor kan increased worden
- Safe: Resistent against brute-force
- Industry standard: OWASP approved

---

### 2. Content Moderation via Gemini API

**Beslissing:** Google Gemini API for text moderation.

```java
@Service
public class ModerationService {
    public ModerationResult moderateText(String text) {
        Response response = geminiApi.call(text)
        boolean isBlocked = response.isProfane()
        return new ModerationResult(isBlocked, response.reason)
    }
}
```

**Voordelen:**
- Cloud-based: Niet zelf trainen
- Context-aware: AI kan nuances verstaan
- Scalable: Geen local model nodig

---

## Performance & Optimization Decisions

### 1. Lazy Loading Routes in Vue

**Beslissing:** Route components lazy-loaded.

```typescript
const routes = [
    {
        path: '/voting-guide',
        component: () => import('@/views/VotingGuide.vue')
    },
    {
        path: '/admin',
        component: () => import('@/views/AdminPanel.vue')
    }
]
```

**Voordelen:**
- Code splitting: Slechts nodig JS laden
- Faster initial load: Index bundle klein
- Better caching: Route changes don't invalidate all

---

### 2. Vite over Webpack

**Beslissing:** Vite voor build tooling.

**Voordelen:**
- Speed: ES modules native, snellere dev server
- HMR: Hot module replacement zeer snel
- Optimized builds: Automatic code splitting
- Modern: Focuses op modern browsers

---

## Testing & Quality Assurance

### 1. Unit Testing Strategy

**Beslissing:** Services unit-tested, controllers tested via MockMvc.

```java
@WebMvcTest(controllers = PollController.class)
class PollControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private PollService pollService;
    
    @Test
    void testCreatePoll() throws Exception {
        mockMvc.perform(post("/api/polls")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJson(request)))
            .andExpect(status().isCreated())
    }
}
```

**Voordelen:**
- Isolation: Service logic separate van HTTP
- Speed: Controllers tested in-memory
- Spring context: Integration testing possible

---

## Database Design Decisions

### 1. Denormalization voor Performance

**Beslissing:** Sommige data is denormalized voor query performance.

```java
// MunicipalityResultEntity stores both party_id and votes
// Instead of separate lookup in partyvotes table
@Entity
public class MunicipalityResultEntity {
    @Id private long id;
    
    private int year;
    private String municipality;
    private String partyId;
    private int votes;  // Denormalized for fast queries
}
```

**Voordelen:**
- Query speed: Direct access, geen joins nodig
- Simplicity: Minder complex queries

**Trade-off:**
- Update complexity: Moet multiple places updaten
- Storage: Duplication

---

### 2. Moderation Queue Design

**Beslissing:** ReactionEntity met moderation status flags.

```java
@Entity
public class ReactionEntity {
    @Id private Long id;
    
    private String content;
    private String status;           // PENDING, APPROVED, REJECTED
    @Column(name = "is_flagged") 
    private boolean flagged;        // AI flagged for review
}
```

**Voordelen:**
- Simple: Single table, easy to query
- Status tracking: Complete audit trail

---

## Summary of Key Decisions

| Aspect | Decision | Rationale |
|--------|----------|-----------|
| Backend | Spring Boot 3.5 | Industry standard, rich ecosystem |
| Frontend | Vue 3 + Composition API | Smaller learning curve, reactive |
| State Management | Pinia | Official Vue solution, type-safe |
| Database | PostgreSQL | Robust, open-source, good support |
| Authentication | JWT + Refresh tokens | Stateless, scalable, secure |
| API Client | Custom fetch wrapper | Lightweight, custom JWT logic |
| Styling | TailwindCSS | Utility-first, optimized bundle |
| Visualization | Chart.js + Leaflet | Specialized tools for their domain |
| Build Tool | Vite | Fast dev experience, optimized builds |
| Testing | Unit + MockMvc | Good coverage, Spring integrated |

---