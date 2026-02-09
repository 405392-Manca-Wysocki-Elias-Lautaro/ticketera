# Copilot Instructions for Ticketera

## Project Overview

**Ticketera** is a microservices-based event ticketing platform with:
- **Backend**: 7 Java Spring Boot microservices (Auth, Event, Ticket, Payment, Order, Notification, Gateway) using Maven + Spring Data JPA + PostgreSQL + RabbitMQ
- **Frontend**: Next.js 15 TypeScript with React 19, Radix UI, TailwindCSS
- **Database**: PostgreSQL with 6 schemas (auth, events, orders, tickets, payments, notifications) + Flyway migrations
- **Orchestration**: Docker Compose for dev/prod + GitHub Actions CI/CD

## Critical Architecture Patterns

### Microservices Structure
- **Gateway** (`backend/gateway`): Spring Cloud Gateway - routes API requests to backend services
- **Auth Service** (`backend/auth-service`): JWT/API key authentication, user/role management
- **Event Service** (`backend/event-service`): Events, occurrences, venues, pricing
- **Ticket Service** (`backend/ticket-service`): Ticket issuance, holds, history
- **Order Service** (`backend/order-service`): Order creation and management
- **Payment Service** (`backend/payment-service`): Payment methods, MercadoPago integration
- **Notification Service** (`backend/notification-service`): Email, QR codes, event outbox pattern

Each service has:
- `src/main/java/com/{service}` - Java code
- `src/main/resources/application-{profile}.yml` - Spring Boot config
- `src/main/resources/db/migration/` - Flyway SQL migrations
- `pom.xml` - Maven dependencies (Java 17, Spring Boot 3.5.6)

### Database Schema Separation
PostgreSQL single instance with 6 independent schemas:
- `auth`: users, roles, organizers, API keys
- `events`: events, occurrences, venues, areas, pricing
- `orders`: customers, orders, line items
- `tickets`: issued tickets, holds, history
- `payments`: payment methods, intents, refunds
- `notifications`: outbox tables (event sourcing pattern)

**Key metadata columns on all tables**: `created_at`, `updated_at`, `deleted_at` (soft delete)

### Frontend Architecture
- `app/` - Next.js App Router with protected/public routes
- `components/` - React components (auth, camera, dashboard, tickets, ui)
- `hooks/` - Custom React hooks (auth, event, ticket business logic)
- `lib/` - Utilities (api.ts with axios client, queryClient, Zustand store)
- `services/` - External service integrations
- `schemas/` - Zod validation schemas

**Key tech stack**: React Query v5, Zustand for state, react-hook-form, Radix UI, TailwindCSS v4

## Essential Workflows

### Local Development Startup
```bash
# 1. Start all services (Docker Compose)
docker-compose up -d

# 2. Frontend (hot reload)
cd frontend && pnpm dev

# 3. Attach to a backend service for debugging
# Gateway already runs via docker-compose, but for debugging:
cd backend/order-service && mvn -Pdev spring-boot:run
```

### Building & Testing
- **Backend**: `mvn clean verify` (runs tests + integration tests with Testcontainers)
- **Frontend**: `pnpm build && pnpm lint`
- **Java version**: 17 (Spring Boot 3.5.6 requirement)

### Database Migrations
- **Tool**: Flyway (runs on service startup)
- **Location**: `backend/{service}/src/main/resources/db/migration/V{number}__{description}.sql`
- **Common issue**: "Detected applied migration not resolved locally" = migration removed locally but exists in DB
  - **Fix**: Run `mvn flyway:repair` or remove the orphaned migration from database

### Docker Compose Profiles
- **Dev**: Hot reload via `spring-boot:run`, Maven cached in volumes, debug ports (5005+), all ports exposed
- **Prod**: Multi-stage JAR builds, minimal `eclipse-temurin:17-jre` runtime, only Gateway/Frontend expose ports

### CI/CD (GitHub Actions)
- **PR validation** (`ci-pr.yml`): Detects changed modules, builds only changed services
- **Release** (`release-*.yml`): semantic-release cuts versions, publishes Docker images to GHCR
- **Branch protection**: `guard-branches.yml` prevents direct pushes to main/develop (PR required)

## Common Patterns & Conventions

### Conventional Commits
Required format (enforced by commitlint):
```
feat(order-service): add refund processing
fix(frontend): prevent double submission
docs: update database schema
```
Scopes: microservice names or generic (docs, ci, etc.)

### Spring Boot Profiles
Services use `application-{profile}.yml`:
- **dev**: Localhost dependencies, debug logging
- **prod**: Environment variables, optimized pooling

Access via: `@Value("${key}")` or `@ConfigurationProperties`

### Entity Patterns (JPA)
- All entities have `@CreationTimestamp`, `@UpdateTimestamp`, `@ColumnDefault("null") deleted_at`
- Repositories extend `JpaRepository<Entity, UUID>`
- Service layer handles business logic (Order → Payment flow coordination)

### Frontend State Management
- **Authentication**: Zustand store (`lib/store.ts`) + React Context from `components/providers`
- **Data fetching**: React Query with custom hooks (`useEvent`, `useTicket`, etc. in `hooks/`)
- **Forms**: react-hook-form + Zod schemas for validation

### Error Handling
- **Backend**: REST endpoints return structured errors with HTTP status codes
- **Frontend**: Axios interceptor handles 401/403, Form validation shows inline errors
- **Database**: Soft delete (never hard delete) to maintain audit trails

## Code Quality & Architecture Standards

### 1. Architecture & Scalability (Horizontal First)

**Stateless Design**: All code must be compatible with multi-instance execution. Prohibit in-memory state or persistent global variables on the server.
- ❌ Bad: `static List<Order> orderCache = new ArrayList<>();`
- ✅ Good: Inject `CacheManager` or use Redis for distributed state

**Dependency Inversion**: Code against interfaces, not implementations. When strong coupling is detected between modules, propose an abstraction layer.
```java
// ❌ Avoid
public class OrderService {
  private PostgresOrderRepository repo = new PostgresOrderRepository();
}

// ✅ Prefer
public class OrderService {
  private final OrderRepository repository; // Interface
  public OrderService(OrderRepository repository) { this.repository = repository; }
}
```

**Asynchronous Processing**: Heavy tasks (email, image processing, long computations) must be delegated to queues/jobs via RabbitMQ by default, not synchronous calls.
- Use `@RabbitListener` + message publishing for order processing, notifications
- Example: Payment confirmation → publish event → Notification service consumes asynchronously

### 2. Code Sustainability (SOLID Enforcement)

**Single Responsibility**: Each class/method has ONE reason to change. Refactor before continuing if multiple concerns exist.
- ❌ Bad: `OrderService` handles order logic + email sending + payment validation
- ✅ Good: Separate `OrderService`, `NotificationPublisher`, `PaymentValidator`

**Open/Closed Principle**: Code is extensible; avoid long if/else chains for business logic. Use Strategy, Factory, or polymorphism.
```java
// ❌ Avoid
if (paymentMethod.equals("MERCADO_PAGO")) { /* process MP */ }
else if (paymentMethod.equals("CREDIT_CARD")) { /* process CC */ }

// ✅ Prefer
interface PaymentProcessor { void process(PaymentIntent intent); }
class MercadoPagoProcessor implements PaymentProcessor { }
Map<String, PaymentProcessor> processors; // Dependency inject
```

**DRY (Don't Repeat Yourself)**: Before generating new code, scan the project for similar logic to reuse or abstract. Example: If multiple services validate email, create `EmailValidator` util.

**Self-Documenting Code**: Use semantic variable/method names. Comments explain *why* a complex decision was made, not *what* the code does.
```java
// ❌ Avoid
int x = orders.size(); // get count

// ✅ Good
int totalOrdersProcessed = orders.size(); // Clear intent
// Strategy: Using eager fetch to prevent N+1 on nested occurrences
```

### 3. Data Efficiency (Performance Guard)

**N+1 Protection**: NEVER execute database queries inside loops. Use Eager Loading (JPA `@EntityGraph`) or Batch operations.
```java
// ❌ Banned
for (Order order : orders) {
  Customer customer = customerService.findById(order.getCustomerId()); // Loop query!
}

// ✅ Correct
List<Order> ordersWithCustomers = orderRepo.findAll(
  EntityGraph.named("Order.withCustomer")
);
```

**Transaction Integrity**: Any operation affecting multiple tables must be wrapped in `@Transactional`. Ensure proper isolation levels.
```java
@Transactional
public Order createOrder(OrderRequest req) {
  Order order = orderRepo.save(new Order());
  lineItemRepo.saveAll(req.getItems()); // Atomic with order creation
  publishOrderCreatedEvent(order);
  return order;
}
```

**Caching Strategy**: For frequent reads of static data, implement/suggest Redis caching. Example: Event details, pricing tiers, venue info.
```java
@Cacheable("events")
public Event getEvent(UUID eventId) { 
  return eventRepo.findById(eventId); 
}
```

### 4. Automated Quality (Zero-Defect Goal)

**Test-Driven Execution**: Generate unit tests in parallel with or before feature code. Minimum coverage: 70% for business logic.
- Backend: JUnit 5 + Mockito for services; Testcontainers for repositories
- Frontend: Jest for hooks/utils, React Testing Library for components
- Command: `mvn clean verify` or `pnpm test`

**Edge Case Handling**: Actively validate "sad paths" via Guard Clauses. Never assume inputs are valid.
```java
// ✅ Guard Clauses
public void processPayment(PaymentIntent intent) {
  if (intent == null) throw new IllegalArgumentException("Payment intent required");
  if (intent.getAmount() <= 0) throw new InvalidAmountException();
  if (intent.getEvent().isExpired()) throw new EventExpiredException();
  
  // Safe to process
  paymentService.charge(intent);
}
```

**Error Propagation**: Use custom exceptions with semantic names. Avoid swallowing exceptions.
```java
// ✅ Good
try {
  publishToQueue(event);
} catch (AmqpException e) {
  throw new TicketingException("Failed to publish order event", e);
}
```

## Key File Locations

| Concern | Location |
|---------|----------|
| Service config | `backend/{service}/src/main/resources/application-{profile}.yml` |
| Migrations | `backend/{service}/src/main/resources/db/migration/` |
| Docker compose | `docker-compose.yml` (dev) or `docker-compose.prod.yml` |
| Release config | `backend/{service}/release.config.js` (semantic-release) |
| Frontend routes | `frontend/app/(protected)/` and `frontend/app/(public)/` |
| Auth logic | `backend/auth-service/` + `frontend/hooks/auth/` |
| API client | `frontend/lib/api.ts` |

## Debugging Tips

1. **Spring Boot service won't start**: Check Flyway migrations - compare DB with local `db/migration/` folder
2. **Frontend API calls fail**: Verify Gateway is running (`docker-compose logs gateway-dev`)
3. **Docker network issues**: Services reference each other by name (`http://auth-dev:8080`), not localhost
4. **Build cache problems**: Use `mvn clean install` or `docker-compose down -v` to reset volumes
5. **Hot reload not working**: Ensure Spring DevTools enabled in pom.xml, check file watcher limits on host

## Testing Approach

- **Unit tests**: JUnit 5 + Mockito (backend), Jest (frontend)
- **Integration tests**: Testcontainers + PostgreSQL (backend)
- **E2E**: Playwright or Cypress (if configured in frontend/)
- **Manual QR testing**: Use `/TEST_ORDER_TO_PAYMENT_FLOW.md` for payment integration flow
