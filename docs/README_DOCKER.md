# Guía Completa de Docker para Ticketera

Esta guía documenta **cómo funciona Docker**, describe **las conexiones entre servicios**, detalla **los `docker-compose` de desarrollo y producción**, explica **los `Dockerfile`** y ofrece un **recetario de comandos** para **levantar, bajar y limpiar** contenedores, imágenes, redes y volúmenes.

> Proyecto: monorepo con **frontend (React)**, **Gateway (Spring Cloud Gateway / WebFlux)** y microservicios **Auth, Event, Ticket, Payment, Notification** (Spring Boot), más **PostgreSQL**. Orquestación con Docker Compose.

---
## 1) Arquitectura Docker

- **Redes**
  - `backend`: interconecta *gateway* + microservicios + base de datos.
  - `frontend_net`: conecta el *frontend* con el *gateway* (exposición HTTP pública).
- **Servicios principales**
  - **PostgreSQL** (persistencia, volumen `pgdata`).
  - **Gateway** (expuesto hacia afuera; enruta a servicios internos).
  - **Frontend** (expuesto para servir la SPA).
  - **Microservicios**: `auth`, `events`, `tickets`, `payments`, `notifications`.
- **Estrategia por entorno**
  - **Dev**: contenedores *builder* (Maven) con *hot reload* (`spring-boot:run`) + puertos de **debug JDWP 5005+** + caché Maven (`m2cache`).
  - **Prod**: imágenes *multi-stage* compiladas (JAR) y *runtime* liviano `eclipse-temurin:17-jre`. Microservicios sin puertos publicados; solo el **gateway** expone `${PUBLIC_HTTP_PORT}:8080` y **frontend** expone `8081:8081`.

Diagrama (simplificado):

```
[Browser] → (host:${PUBLIC_HTTP_PORT}) → [gateway] ──► [auth]
                            │                  ├──► [events]
                            │                  ├──► [tickets]
                            │                  ├──► [payments]
                            │                  └──► [notifications]
                            └── (host:8081) ←── [frontend]
                                     │
                               consume /api/* via gateway
```
> En Docker, cada servicio puede resolver a otro por su **nombre de servicio** (`auth`, `postgres`, etc.) dentro de la misma **red**.

---

## 2) Perfiles y configuración (`dev` vs `prod`)

- **Spring Profiles**: el Gateway y los micros usan `SPRING_PROFILES_ACTIVE` para seleccionar `application-<profile>.yml`.
  - En **dev** (hot reload): `SPRING_PROFILES_ACTIVE=dev`.
  - En **prod** (runtime): `SPRING_PROFILES_ACTIVE=prod`.
- **Gateway (dev)** toma las URLs destino desde **variables de entorno** con *fallback* seguro (ver abajo). Ejemplo de rutas activas:

- **auth** → `${SERVICES_AUTH_BASE_URL:http://auth-dev:8080}` 
  - Predicados: Path=/api/auth/**
  - Filtros: RewritePath=/api/auth/?(?<segment>.*), /${segment}
- **events** → `${SERVICES_EVENT_BASE_URL:http://event-dev:8080}` 
  - Predicados: Path=/api/events/**
  - Filtros: RewritePath=/api/events/?(?<segment>.*), /${segment}
- **tickets** → `${SERVICES_TICKET_BASE_URL:http://ticket-dev:8080}` 
  - Predicados: Path=/api/tickets/**
  - Filtros: RewritePath=/api/tickets/?(?<segment>.*), /${segment}
- **payments** → `${SERVICES_PAYMENT_BASE_URL:http://payment-dev:8080}` 
  - Predicados: Path=/api/payments/**
  - Filtros: RewritePath=/api/payments/?(?<segment>.*), /${segment}
- **notifications** → `${SERVICES_NOTIFICATION_BASE_URL:http://notification-dev:8080}` 
  - Predicados: Path=/api/notifications/**
  - Filtros: RewritePath=/api/notifications/?(?<segment>.*), /${segment}

> Nota: Evitá defaults inválidos (p.ej. `http:`). Si falta la env, usá un fallback **completo** (`http://auth-dev:8080`).

- **Microservicios** (ejemplo `auth`): el `application.yml` usa variables para el datasource y hace *fallback* a valores de dev.
```yaml
spring:
  application:
    name: auth   # cambia por el nombre del micro
  datasource:
    # leemos de env; si falta, usamos defaults de dev
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://postgres:5432/appdb}
    username: ${SPRING_DATASOURCE_USERNAME:${POSTGRES_USER:appuser}}
    password: ${SPRING_DATASOURCE_PASSWORD:${POSTGRES_PASSWORD:apppass}}
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update   # solo para dev
    show-sql: true       # opcional

# NO fijamos server.port -> queda en 8080 (default)

```

---

## 3) `docker-compose` de **Desarrollo**

**Resumen de servicios (dev)**

| Servicio         | Imagen/Build                   | Puertos              | Redes                 | Depende de                                                     |
| ---------------- | ------------------------------ | -------------------- | --------------------- | -------------------------------------------------------------- |
| postgres         | postgres:16                    | 5432:5432            | backend               |                                                                |
| frontend-dev     | ${NODE_IMAGE:-node:20}         | 5173:5173            | frontend_net, backend |                                                                |
| gateway-dev      | maven:3.9.8-eclipse-temurin-17 | 8080:8080, 5005:5005 | backend               | auth-dev, event-dev, ticket-dev, payment-dev, notification-dev |
| auth-dev         | maven:3.9.8-eclipse-temurin-17 | 8082:8080, 5006:5005 | backend               | postgres                                                       |
| event-dev        | maven:3.9.8-eclipse-temurin-17 | 8083:8080, 5007:5005 | backend               | postgres                                                       |
| notification-dev | maven:3.9.8-eclipse-temurin-17 | 8086:8080, 5008:5005 | backend               | postgres                                                       |
| payment-dev      | maven:3.9.8-eclipse-temurin-17 | 8085:8080, 5009:5005 | backend               | postgres                                                       |
| ticket-dev       | maven:3.9.8-eclipse-temurin-17 | 8084:8080, 5010:5005 | backend               | postgres                                                       |
| order-dev        | maven:3.9.8-eclipse-temurin-17 | 8087:8080, 5010:5005 | backend               | postgres                                                       |

**Archivo**: `docker-compose.dev.yml`

```yaml
networks:
  backend:
  frontend_net:

volumes:
  m2cache:
  pgdata:

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_USER: ${POSTGRES_USER:-app}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-app}
      POSTGRES_DB: ${APP_DB:-app}
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
      - ./_init:/docker-entrypoint-initdb.d:ro
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-app} -d ${APP_DB:-app}"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks: [backend]

  frontend-dev:
    image: ${NODE_IMAGE:-node:20}
    working_dir: /app
    command: sh -c "npm ci && npm run dev -- --host 0.0.0.0 --port 5173"
    volumes:
      - ./frontend:/app
    ports:
      - "5173:5173"
    networks: [ frontend_net, backend ]

  gateway-dev:
    image: maven:3.9.8-eclipse-temurin-17
    working_dir: /workspace
    command: >
      bash -lc "
        mvn -q -DskipTests dependency:go-offline &&
        mvn -q -Dspring-boot.run.fork=false spring-boot:run
      "
    volumes:
      - ./backend/gateway:/workspace
      - m2cache:/root/.m2
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SERVICES_AUTH_BASE_URL: http://auth-dev:8080
      SERVICES_EVENT_BASE_URL: http://event-dev:8080
      SERVICES_TICKET_BASE_URL: http://ticket-dev:8080
      SERVICES_PAYMENT_BASE_URL: http://payment-dev:8080
      SERVICES_NOTIFICATION_BASE_URL: http://notification-dev:8080
    depends_on:
      - auth-dev
      - event-dev
      - ticket-dev
      - payment-dev
      - notification-dev
    networks: [backend]
    ports:
      - "8080:8080"
      - "5005:5005"

  auth-dev:
    image: maven:3.9.8-eclipse-temurin-17
    working_dir: /workspace
    command: >
      bash -lc "
        mvn -q -DskipTests dependency:go-offline &&
        mvn -q -Dspring-boot.run.fork=false spring-boot:run
      "
    volumes:
      - ./backend/auth-service:/workspace
      - m2cache:/root/.m2
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${AUTH_DB:-auth}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER:-app}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-app}
    depends_on: [postgres]
    networks: [backend]
    ports:
      - "8082:8080"
      - "5006:5005"

  event-dev:
    image: maven:3.9.8-eclipse-temurin-17
    working_dir: /workspace
    command: >
      bash -lc "
        mvn -q -DskipTests dependency:go-offline &&
        mvn -q -Dspring-boot.run.fork=false spring-boot:run
      "
    volumes:
      - ./backend/event-service:/workspace
      - m2cache:/root/.m2
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${EVENTS_DB:-events}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER:-app}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-app}
    depends_on: [postgres]
    networks: [backend]
    ports:
      - "8083:8080"
      - "5007:5005"

  notification-dev:
    image: maven:3.9.8-eclipse-temurin-17
    working_dir: /workspace
    command: >
      bash -lc "
        mvn -q -DskipTests dependency:go-offline &&
        mvn -q -Dspring-boot.run.fork=false spring-boot:run
      "
    volumes:
      - ./backend/notification-service:/workspace
      - m2cache:/root/.m2
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${NOTIFICATIONS_DB:-notifications}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER:-app}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-app}
    depends_on: [postgres]
    networks: [backend]
    ports:
      - "8086:8080"
      - "5008:5005"

  payment-dev:
    image: maven:3.9.8-eclipse-temurin-17
    working_dir: /workspace
    command: >
      bash -lc "
        mvn -q -DskipTests dependency:go-offline &&
        mvn -q -Dspring-boot.run.fork=false spring-boot:run
      "
    volumes:
      - ./backend/payment-service:/workspace
      - m2cache:/root/.m2
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${PAYMENTS_DB:-payments}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER:-app}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-app}
    depends_on: [postgres]
    networks: [backend]
    ports:
      - "8085:8080"
      - "5009:5005"

  ticket-dev:
    image: maven:3.9.8-eclipse-temurin-17
    working_dir: /workspace
    command: >
      bash -lc "
        mvn -q -DskipTests dependency:go-offline &&
        mvn -q -Dspring-boot.run.fork=false spring-boot:run
      "
    volumes:
      - ./backend/ticket-service:/workspace
      - m2cache:/root/.m2
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${TICKETS_DB:-tickets}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER:-app}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-app}
    depends_on: [postgres]
    networks: [backend]
    ports:
      - "8084:8080"
      - "5010:5005"

  order-dev:
    image: maven:3.9.8-eclipse-temurin-17
    working_dir: /workspace
    command: >
      bash -lc "
        mvn -q -DskipTests dependency:go-offline &&
        mvn -q -Dspring-boot.run.fork=false spring-boot:run
      "
    volumes:
      - ./backend/order-service:/workspace
      - m2cache:/root/.m2
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${ORDERS_DB:-orders}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER:-app}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-app}
    depends_on: [postgres]
    networks: [backend]
    ports:
      - "8084:8080"
      - "5010:5005"

```

Puntos clave:
- **Hot reload**: servicios `*-dev` usan imagen `maven:3.9.8-eclipse-temurin-17` y montan el código en `/workspace` para `spring-boot:run`.
- **Debug remoto**: cada micro publica `5005+` (gateway `5005`, auth `5006`, event `5007`, ...).
- **DB**: `postgres:16` en `5432`, volumen `pgdata`, scripts iniciales opcionales.
- **Gateway** resuelve a `*-dev:8080` vía envs `SERVICES_*_BASE_URL`.

**Variables disponibles en dev** (ejemplo `.env.dev`):

```
APP_DB=
AUTH_DB=
EVENTS_DB=
NODE_IMAGE=
NOTIFICATIONS_DB=
PAYMENTS_DB=
POSTGRES_PASSWORD=
POSTGRES_USER=
TICKETS_DB=
ORDERS_DB=
```

---

## 4) `docker-compose` de **Producción**

**Resumen de servicios (prod)**

| Servicio      | Imagen/Build                   | Puertos                  | Redes                 | Depende de                                     |
| ------------- | ------------------------------ | ------------------------ | --------------------- | ---------------------------------------------- |
| postgres      | ${POSTGRES_IMAGE}              | 5432:5432                | backend               |                                                |
| auth          | ./backend/auth-service         |                          | backend               | postgres                                       |
| events        | ./backend/event-service        |                          | backend               | postgres                                       |
| tickets       | ./backend/ticket-service       |                          | backend               | postgres                                       |
| orders        | ./backend/orders-service       |                          | backend               | postgres                                       |
| payments      | ./backend/payment-service      |                          | backend               | postgres                                       |
| notifications | ./backend/notification-service |                          | backend               | postgres                                       |
| frontend      | ./frontend                     | 8081:8081                | frontend_net          |                                                |
| gateway       | ./backend/gateway              | ${PUBLIC_HTTP_PORT}:8080 | backend, frontend_net | auth, events, tickets, payments, notifications |

**Archivo**: `docker-compose.yml`

```yaml
services:
  postgres:
    image: ${POSTGRES_IMAGE}
    container_name: db
    environment:
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "5432:5432"   
    volumes:
      - pgdata:/var/lib/postgresql/data
      - ./_init/sql:/docker-entrypoint-initdb.d
    networks: [ backend ]

  # rabbitmq:
  #   image: ${RABBIT_IMAGE}
  #   container_name: rabbit
  #   environment:
  #     RABBITMQ_DEFAULT_USER: ${RABBIT_USER}
  #     RABBITMQ_DEFAULT_PASS: ${RABBIT_PASS}
  #   ports:
  #     - "15672:15672"
  #   networks: [ backend ]

  # === Microservicios ===
  auth:
    build:
      context: ./backend/auth-service
      dockerfile: ./Dockerfile
      args:
        JDK_IMAGE: ${JDK_IMAGE}
        RUNTIME_IMAGE: eclipse-temurin:17-jre
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${AUTH_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      # RABBITMQ_HOST: rabbitmq
      # RABBITMQ_USER: ${RABBIT_USER}
      # RABBITMQ_PASS: ${RABBIT_PASS}
    depends_on: [ postgres ]
    networks: [ backend ]

  events:
    build:
      context: ./backend/event-service
      dockerfile: ./Dockerfile
      args:
        JDK_IMAGE: ${JDK_IMAGE}
        RUNTIME_IMAGE: eclipse-temurin:17-jre
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${EVENT_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      # RABBITMQ_HOST: rabbitmq
      # RABBITMQ_USER: ${RABBIT_USER}
      # RABBITMQ_PASS: ${RABBIT_PASS}
    depends_on: [ postgres ]
    networks: [ backend ]

  tickets:
    build:
      context: ./backend/ticket-service
      dockerfile: ./Dockerfile
      args:
        JDK_IMAGE: ${JDK_IMAGE}
        RUNTIME_IMAGE: eclipse-temurin:17-jre
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${TICKET_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      # RABBITMQ_HOST: rabbitmq
      # RABBITMQ_USER: ${RABBIT_USER}
      # RABBITMQ_PASS: ${RABBIT_PASS}
    depends_on: [ postgres ]
    networks: [ backend ]

  payments:
    build:
      context: ./backend/payment-service
      dockerfile: ./Dockerfile
      args:
        JDK_IMAGE: ${JDK_IMAGE}
        RUNTIME_IMAGE: eclipse-temurin:17-jre
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${PAYMENTS_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      # RABBITMQ_HOST: rabbitmq
      # RABBITMQ_USER: ${RABBIT_USER}
      # RABBITMQ_PASS: ${RABBIT_PASS}
    depends_on: [ postgres ]
    networks: [ backend ]

  notifications:
    build:
      context: ./backend/notification-service
      dockerfile: ./Dockerfile
      args:
        JDK_IMAGE: ${JDK_IMAGE}
        RUNTIME_IMAGE: eclipse-temurin:17-jre
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${NOTIF_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      # RABBITMQ_HOST: rabbitmq
      # RABBITMQ_USER: ${RABBIT_USER}
      # RABBITMQ_PASS: ${RABBIT_PASS}
    depends_on: [ postgres ]
    networks: [ backend ]

  # Frontend estático servido por Node 'serve'
  frontend:
    build:
      context: ./frontend
    ports:
      - "8081:8081"
    networks: [ frontend_net ]

  # API Gateway WebFlux (Spring Cloud Gateway)
  gateway:
    build:
      context: ./backend/gateway
      dockerfile: ./Dockerfile
      args:
        JDK_IMAGE: ${JDK_IMAGE}
        RUNTIME_IMAGE: eclipse-temurin:17-jre
    environment:
      SPRING_PROFILES_ACTIVE: prod
      # CORS_ALLOWED_ORIGINS: "http://localhost:5173,http://localhost:8081"
    depends_on: [ auth, events, tickets, payments, notifications ]
    ports:
      - "${PUBLIC_HTTP_PORT}:8080"
    networks: [ backend, frontend_net ]

volumes:
  pgdata:

networks:
  backend:
  frontend_net:

```

Puntos clave:
- **Build multi-stage** de cada micro desde su contexto (`./backend/*-service`). El Gateway también se construye.
- **Puertos expuestos**:
  - `gateway`: `${PUBLIC_HTTP_PORT}:8080` (HTTP público).
  - `frontend`: `8081:8081` (sirve la SPA).
  - Microservicios **no exponen** puertos hacia el host — se consumen detrás del gateway.
- **PostgreSQL** usa `${POSTGRES_IMAGE}` y credenciales `${POSTGRES_USER}/${POSTGRES_PASSWORD}` con volumen `pgdata`.
- **Redes**: `backend` y `frontend_net` para segmentar tráfico.

**Variables requeridas en prod** (ejemplo `.env`):

```
AUTH_DB=
EVENT_DB=
JDK_IMAGE=
NOTIF_DB=
PAYMENTS_DB=
POSTGRES_IMAGE=
POSTGRES_PASSWORD=
POSTGRES_USER=
PUBLIC_HTTP_PORT=
RABBIT_IMAGE=
RABBIT_PASS=
RABBIT_USER=
TICKET_DB=
```

---

## 5) `Dockerfile` (plantilla base Spring Boot)

Todos los microservicios comparten una **estructura multi-stage**: `deps` → `dev` (hot reload) → `build` → `prod` (runtime).

```dockerfile
# ===== deps (cache m2) =====
FROM maven:3.9.8-eclipse-temurin-17 AS deps
WORKDIR /workspace
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

# ===== dev (hot reload) =====
FROM maven:3.9.8-eclipse-temurin-17 AS dev
WORKDIR /workspace
ENV SPRING_PROFILES_ACTIVE=dev
# Debug remoto opcional (JDWP)
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
# en dev montás el código como volumen; no copiamos src
EXPOSE 8080 5005
ENTRYPOINT ["sh","-c","exec mvn -q -Dspring-boot.run.profiles=${SPRING_PROFILES_ACTIVE:-dev} spring-boot:run"]

# ===== build (prod) =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests clean package

# ===== prod (runtime) =====
FROM eclipse-temurin:17-jre AS prod
WORKDIR /app
RUN groupadd -r app && useradd -r -g app app
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/urandom"
COPY --from=build /app/target/*.jar /app/app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]

```

Notas:
- En **dev** se expone `8080 5005` y se corre `spring-boot:run`. Se monta el código (volumen) desde el host.
- En **prod** se copia el **JAR** y se corre con `java -jar`, usuario no root, flags `JAVA_OPTS` seguros.
- El Gateway y los micros usan `SPRING_PROFILES_ACTIVE` para seleccionar su perfil.

---

## 6) Conexiones y resolución de nombres

- **Resolución DNS interna**: Docker Compose crea DNS por **nombre de servicio**. Ej.: `jdbc:postgresql://postgres:5432/...` es válido desde cualquier micro unido a `backend`.
- **Gateway → Micros**: en dev por `http://<micro>-dev:8080`; en prod por `http://<micro>:8080` (según variables/archivos de perfil).
- **Frontend → Gateway**: el frontend hace requests a `/api/...` contra `{host}:${PUBLIC_HTTP_PORT}` (y el Gateway reescribe el path con `RewritePath`).

---

## 7) Recetario de comandos (levantar, bajar, limpiar)

> Todos los comandos se ejecutan desde la raíz del repo.

### 7.1 Desarrollo

- **Levantar todo en segundo plano**  
  ```bash
  docker compose up -d --build
  ```
  - **Levantar y reconstruir**
  ```bash
  docker compose up -d --build
  ```
  - **Levantar y reconstruir un servicio**
  ```bash
  docker compose up -d --build gateway-dev
  ```
  - **Reconstruir (si cambiaste base de imagen/cachés)**
  ```bash
  docker compose -d --build --no-deps gateway-dev
  ```
- **Ver logs en vivo (gateway)(verás Flyway migrando)**    
  ```bash
  docker compose logs -f gateway-dev
  ```
  **Ver logs en vivo (postgres)**  
  ```bash
  docker compose logs -f postgres
  ```
- **Entrar al contenedor (shell)**
  ```bash
  docker compose exec gateway-dev sh
  ```
- **Reconstruir (si cambiaste base de imagen/cachés)**
  ```bash
  docker compose -d --build --no-deps gateway-dev
  ```
- **Bajar todo (manteniendo volúmenes)**  
  ```bash
  docker compose down
  ```
- **Bajar todo y borrar volúmenes (⚠ destruye datos de DB)**  
  ```bash
  docker compose down -v --remove-orphans
  ```

### 7.2 Producción / Build local

- **Compilar y levantar**  
  ```bash
  # Asegurate de tener .env con POSTGRES_IMAGE, JDK_IMAGE, PUBLIC_HTTP_PORT, etc.
  docker compose --env-file .env.prod -f docker-compose.prod.yml up -d --build
  ```
- **Ver estado**  
  ```bash
  docker compose --env-file .env.prod -f docker-compose.prod.yml ps
  docker compose --env-file .env.prod -f docker-compose.prod.yml logs -f gateway
  ```
- **Bajar stack**  
  ```bash
  docker compose --env-file .env.prod -f docker-compose.prod.yml down
  ```

### 7.3 Limpieza de recursos

- **Listar**  
  ```bash
  docker ps -a
  docker images
  docker volume ls
  docker network ls
  ```
- **Eliminar contenedores parados**  
  ```bash
  docker container prune -f
  ```
- **Eliminar imágenes sin usar**  
  ```bash
  docker image prune -f            # solo dangling
  docker image prune -a -f         # ⚠ elimina TODAS las no usadas por contenedores
  ```
- **Eliminar volúmenes sin usar (⚠ datos)**  
  ```bash
  docker volume prune -f
  ```
- **Eliminar redes sin usar**  
  ```bash
  docker network prune -f
  ```
- **Error típico**: *“Resource is still in use”* al bajar redes  
  ```bash
  # 1) Forzá bajar con órfanos y volúmenes
  docker compose -f docker-compose.dev.yml down -v --remove-orphans
  # 2) Si persiste: identificá qué contenedor usa la red
  docker network inspect ticketera_backend
  docker ps --filter network=ticketera_backend
  # 3) Forzá desconexión/eliminación
  docker network disconnect -f ticketera_backend <container_id>
  docker network rm ticketera_backend
  ```

---

## 8) Puertos y Debug (JDWP)

- **Gateway (dev)**: `8080` (HTTP), `5005` (debug).  
  Conectar desde IDE: *Remote JVM Debug* → host `localhost`, puerto `5005`.
- **Micros (dev)**: `auth 8082/5006`, `event 8083/5007`, `ticket 8084/5010`, `payment 8085/5009`, `notification 8086/5008`.
- **Postgres**: `5432` (para conectarte con DBeaver desde host).

---

## 9) Base de datos y seeds

- Volumen **`pgdata`** persiste datos entre reinicios.
- Scripts en **`./_init/sql`** se montan en `/docker-entrypoint-initdb.d` (prod) para inicializar schemas/datos la **primera vez**.

---

## 10) Buenas prácticas y gotchas

- **Defaults válidos**: al usar `${VAR:default}` asegurate que `default` sea una URL completa o valor útil (p. ej. `http://auth-dev:8080`). Evitá `http:` (rompe el parser URI).
- **`application-*.yml`**: mantené `application.yml` como base y solo diferencias en `application-dev.yml` / `application-prod.yml`.
- **Cache Maven**: en dev usá volumen `m2cache` para acelerar builds.
- **No publiques puertos internos** en prod más allá de gateway/frontend. Menos superficie de ataque.
- **Logs**: usá `docker compose logs -f <svc>` y **health endpoints** (`/health`) para checks rápidos.

---

## 11) Anexos

### 11.1 Árbol esperado (resumen)
```
/backend/
  gateway/
  auth-service/
  event-service/
  ticket-service/
  payment-service/
  notification-service/
/frontend/
docker-compose.dev.yml
docker-compose.yml
.env               (prod)
.env.dev           (opcional, para dev)
```

### 11.2 Plantilla `.env` (prod) mínima
```
AUTH_DB=
EVENT_DB=
JDK_IMAGE=
NOTIF_DB=
PAYMENTS_DB=
POSTGRES_IMAGE=
POSTGRES_PASSWORD=
POSTGRES_USER=
PUBLIC_HTTP_PORT=
RABBIT_IMAGE=
RABBIT_PASS=
RABBIT_USER=
TICKET_DB=
ORDER_DB=
```

---

> Fin de la guía.
