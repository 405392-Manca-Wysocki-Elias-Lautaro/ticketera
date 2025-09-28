# Documentación de Migraciones – Proyecto Ticketera


> Última actualización: 2025-09-28 20:38

Esta guía explica **cómo funcionan las migraciones de base de datos** en Ticketera usando **Flyway** con **PostgreSQL** en un **modelo de DB única + schemas por servicio**. Incluye estructura de carpetas, configuración de Spring Boot, uso en Docker Compose, ejecución en CI (GitHub Actions), comandos útiles y *runbooks* comunes.

---

## Índice
- [Arquitectura: DB única + schemas](#arquitectura-db-única--schemas)
- [Extensión UUID y `init.sql` (infra)](#extensión-uuid-y-initsql-infra)
- [Estructura de migraciones por servicio](#estructura-de-migraciones-por-servicio)
- [Configuración por microservicio (Spring Boot)](#configuración-por-microservicio-spring-boot)
- [Docker Compose (dev y prod)](#docker-compose-dev-y-prod)
- [Workflows CI (PR) con Flyway](#workflows-ci-pr-con-flyway)
- [Comandos y operaciones comunes](#comandos-y-operaciones-comunes)
- [Runbooks](#runbooks)
  - [Crear una nueva migración](#crear-una-nueva-migración)
  - [Añadir un nuevo schema](#añadir-un-nuevo-schema)
  - [Migrar desde multi-DB a DB única](#migrar-desde-multi-db-a-db-única)
  - [Índices CONCURRENTLY](#índices-concurrently)
- [Troubleshooting](#troubleshooting)
- [Checklist de verificación](#checklist-de-verificación)

---

## Arquitectura: DB única + schemas

- **Una sola base** por entorno (dev: `ticketera_dev`, prod: `ticketera_prod`).  
- **Un schema por servicio**: `auth`, `events`, `tickets`, `payments`, `notifications`, y `common` para utilitarios.
- Cada micro **opera sólo en su schema** (config en Spring), y **Flyway** deja su historial en una tabla por schema (`flyway_history_<schema>`).

Ventajas:
- Aislamiento lógico por servicio sin multiplicar servidores/DBs.
- Backups y observabilidad centralizados.
- Migraciones independientes por equipo (cada micro gestiona su carpeta).

---

## Extensión UUID y `init.sql` (infra)

El archivo `infra/postgres/<env>/init.sql` se ejecuta **solo al inicializar** el volumen de datos de Postgres. Define:
1. **Extensión** `pgcrypto` → `public.gen_random_uuid()` para PK UUID.
2. **Schemas** de todos los servicios.
3. **Permisos** y **default privileges** para el rol de la app.

**Ejemplo (dev):**

```sql
\set ON_ERROR_STOP on
BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS events;
CREATE SCHEMA IF NOT EXISTS tickets;
CREATE SCHEMA IF NOT EXISTS payments;
CREATE SCHEMA IF NOT EXISTS notifications;
CREATE SCHEMA IF NOT EXISTS common;

GRANT USAGE, CREATE ON SCHEMA auth, events, tickets, payments, notifications, common TO CURRENT_USER;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES
  IN SCHEMA auth, events, tickets, payments, notifications, common TO CURRENT_USER;
GRANT USAGE, SELECT, UPDATE ON ALL SEQUENCES
  IN SCHEMA auth, events, tickets, payments, notifications, common TO CURRENT_USER;

ALTER DEFAULT PRIVILEGES IN SCHEMA auth          GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO CURRENT_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA events        GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO CURRENT_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA tickets       GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO CURRENT_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA payments      GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO CURRENT_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA notifications GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO CURRENT_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA common        GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO CURRENT_USER;

ALTER DEFAULT PRIVILEGES IN SCHEMA auth          GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO CURRENT_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA events        GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO CURRENT_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA tickets       GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO CURRENT_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA payments      GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO CURRENT_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA notifications GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO CURRENT_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA common        GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO CURRENT_USER;

COMMIT;
```

> En **prod** se recomienda además `REVOKE ALL ON SCHEMA ... FROM PUBLIC;` para endurecer.

---

## Estructura de migraciones por servicio

```
src/main/resources/db/
  migration/
    auth/
      V1__init.sql
      V2__add_indexes.sql
      V3__fk_constraints.sql
    events/
      V1__init.sql
      ...
  r/
    auth/
      R__views_and_functions.sql
    events/
      R__reporting_views.sql
  seed/
    auth/
      V999__seed_minima.sql   # opcional, ideal solo dev/CI
```

- `migration/` → **versionadas**: `V1__...`, `V2__...`. Se ejecutan **una vez** y en **orden**.
- `r/` → **repeatables**: `R__...`. Se re-ejecutan si cambia el contenido.
- `seed/` → datos de ejemplo/idempotentes. Usar en dev/CI; evitar en prod.

**Buenas prácticas:**
- Calificar SIEMPRE con schema (`auth.users`).
- PK UUID: `uuid DEFAULT public.gen_random_uuid()`.
- Una “unidad de cambio” por migración.
- Índices grandes: `CONCURRENTLY` + archivo `.conf` (ver más abajo).

---

## Configuración por microservicio (Spring Boot)

**Variables por micro** (via Compose o env del proceso):
- `DB_SCHEMA=auth|events|tickets|payments|notifications`
- `FLYWAY_TABLE=flyway_history_<schema>` (opcional; claridad)

**`application-<profile>.yml` (ej. dev, auth):**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${{DB_HOST:postgres}}:${{DB_PORT:5432}}/${{POSTGRES_DB}}?currentSchema=${{DB_SCHEMA:auth}},public
    username: ${{POSTGRES_USER}}
    password: ${{POSTGRES_PASSWORD}}
    hikari:
      schema: ${{DB_SCHEMA:auth}}

  jpa:
    hibernate:
      ddl-auto: validate
      default_schema: ${{DB_SCHEMA:auth}}
    properties:
      hibernate.default_schema: ${{DB_SCHEMA:auth}}
      hibernate.dialect: org.hibernate.dialect.PostgreSQLDialect
    open-in-view: false

  flyway:
    enabled: true
    schemas: ${{DB_SCHEMA:auth}}
    default-schema: ${{DB_SCHEMA:auth}}
    table: ${{FLYWAY_TABLE:flyway_history_auth}}
    validate-on-migrate: true
    baseline-on-migrate: false
    locations:
      - classpath:db/migration/auth
      - classpath:db/r/auth
```

> Notá `currentSchema=${{DB_SCHEMA}},public` para que SQL crudo vea `public.gen_random_uuid()`.

---

## Docker Compose (dev y prod)

**Postgres (dev)**
```yaml
postgres:
  image: ${{POSTGRES_IMAGE:-postgres:16-alpine}}
  environment:
    POSTGRES_DB: ${{POSTGRES_DB}}
    POSTGRES_USER: ${{POSTGRES_USER}}
    POSTGRES_PASSWORD: ${{POSTGRES_PASSWORD}}
    TZ: ${{TZ:-America/Argentina/Cordoba}}
  ports: ["${{PG_PORT:-5432}}:5432"]
  volumes:
    - ${{PGDATA_VOL:-ticketera_dev_pgdata}}:/var/lib/postgresql/data
    - ./infra/postgres/dev/init.sql:/docker-entrypoint-initdb.d/00-init.sql:ro
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U ${{POSTGRES_USER}} -d ${{POSTGRES_DB}}"]
    interval: 5s
    timeout: 3s
    retries: 20
```

**Auth (dev, ejemplo)**
```yaml
auth-dev:
  environment:
    SPRING_PROFILES_ACTIVE: dev
    POSTGRES_DB: ${{POSTGRES_DB}}
    POSTGRES_USER: ${{POSTGRES_USER}}
    POSTGRES_PASSWORD: ${{POSTGRES_PASSWORD}}
    DB_SCHEMA: auth
    FLYWAY_TABLE: flyway_history_auth
  depends_on:
    postgres:
      condition: service_healthy
```

**Prod**: igual patrón, sin exponer `5432` salvo necesidad.

---

## Workflows CI (PR) con Flyway

**Estrategia recomendada (GitHub Actions):**
1. Levantar Postgres efímero (`services:`).
2. Ejecutar `infra/postgres/dev/init.sql`.
3. Por cada servicio, ejecutar **Flyway `migrate`** sobre **su schema**.
4. Compilar y correr tests del servicio con `DB_SCHEMA` correspondiente.
5. (Opcional) Build Docker **solo de lo que cambió**.

**Snippet de migraciones por schema (Maven + Flyway):**
```bash
./mvnw -q -f backend/auth-service/pom.xml -DskipTests   -Dflyway.url=jdbc:postgresql://localhost:5432/ticketera_ci   -Dflyway.user=ticketera_app -Dflyway.password=ci_pass   -Dflyway.schemas=auth -Dflyway.defaultSchema=auth   -Dflyway.table=flyway_history_auth   -Dflyway.locations=classpath:db/migration/auth,classpath:db/r/auth   org.flywaydb:flyway-maven-plugin:migrate
```

---

## Comandos y operaciones comunes

### Ver estado de migraciones
- **Flyway (Maven plugin):**  
  `./mvnw -q -f backend/auth-service/pom.xml -Dflyway.* org.flywaydb:flyway-maven-plugin:info`

### Ejecutar migraciones
- **Con la app**: al iniciar cada micro (Spring ejecuta Flyway antes de Hibernate).  
- **Manual (Maven)**: ver snippet arriba.

### Baseline (si ya tenés tablas sin Flyway)
- Temporalmente: `spring.flyway.baseline-on-migrate=true` (una sola vez)  
  o crear `V1__baseline.sql`.

### Repair (checksums rotos por editar una V aplicada)
- `org.flywaydb:flyway-maven-plugin:repair`  
  > Preferible crear una `V` nueva correctiva en lugar de editar una aplicada.

### Out-of-order (integraciones tardías)
- `spring.flyway.out-of-order=true` permite aplicar una versión “vieja” que apareció luego. Úsalo con criterio.

---

## Runbooks

### Crear una nueva migración
1. Elegí el módulo y schema: p. ej. `backend/ticket-service`, `tickets`.
2. Crea el archivo: `src/main/resources/db/migration/tickets/V3__add_table_x.sql`.
3. Escribe SQL **calificado** con schema. Ejemplo:
   ```sql
   CREATE TABLE IF NOT EXISTS tickets.foo (
     id uuid PRIMARY KEY DEFAULT public.gen_random_uuid(),
     created_at timestamptz NOT NULL DEFAULT now()
   );
   ```
4. Commit y PR → el CI validará y aplicará en DB efímera.

### Añadir un nuevo schema (p. ej. `marketing`)
- **Dev**: agregar al `infra/postgres/dev/init.sql` y recrear volumen (`down -v`), o ejecutar un patch SQL con `CREATE SCHEMA ...` + grants + default privileges.  
- **Prod**: patch SQL controlado (no depende del init).  
- Config del micro nuevo: `DB_SCHEMA=marketing` y migras en `db/migration/marketing`.

### Migrar desde multi-DB a DB única
1. `pg_dump` por DB antigua y forzar schema con `sed 's/public\./auth./g'` (ajustar).  
2. Importar sobre `ticketera_<env>`.  
3. Activar Flyway con `baseline-on-migrate=true` o `V1__baseline.sql`.  
4. Actualizar micros a `currentSchema=${{DB_SCHEMA}},public`.

### Índices CONCURRENTLY
- `CREATE INDEX CONCURRENTLY` no admite transacción.  
- Poner al lado un `.conf` con:  
  `executeInTransaction=false`

Ejemplo:
```
db/migration/events/V2__add_indexes.sql
db/migration/events/V2__add_indexes.sql.conf
```

---

## Troubleshooting

- **`relation "x" does not exist`**  
  Falta calificar schema o la URL no tiene `currentSchema=${{DB_SCHEMA}},public`.

- **`function gen_random_uuid() does not exist`**  
  Falta `pgcrypto` o falta `,public` en `currentSchema`.

- **`cannot run inside a transaction`**  
  Usaste `CONCURRENTLY` sin `.conf` (`executeInTransaction=false`).

- **`checksum mismatch`**  
  Corré `flyway repair` y **no** edites migraciones ya aplicadas; crea una nueva versión.

- **`permission denied`**  
  Revisar `GRANT` y `ALTER DEFAULT PRIVILEGES` del `init.sql`.

---

## Checklist de verificación

- [ ] `init.sql` crea schemas, `pgcrypto`, permisos y default privileges.
- [ ] Cada micro usa `currentSchema=${{DB_SCHEMA}},public` y `hibernate.default_schema`.
- [ ] Flyway configurado con `schemas`, `default-schema`, `table` y `locations` por schema.
- [ ] Migraciones versionadas por carpeta `db/migration/<schema>` y repeatables en `db/r/<schema>`.
- [ ] CI levanta Postgres, aplica `init.sql`, migra **por schema** y corre tests.
- [ ] Seeds solo en dev/CI (idempotentes).
- [ ] En prod, `clean-disabled=true` y no se exponen puertos de Postgres innecesariamente.

---

## Apéndice: ejemplos de V1

**Events – `db/migration/events/V1__init.sql`**
```sql
CREATE TABLE IF NOT EXISTS events.organizers (
  id uuid PRIMARY KEY DEFAULT public.gen_random_uuid(),
  name text NOT NULL,
  slug text NOT NULL UNIQUE,
  contact_email text,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS events.categories (
  id uuid PRIMARY KEY DEFAULT public.gen_random_uuid(),
  name text NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events.events (
  id uuid PRIMARY KEY DEFAULT public.gen_random_uuid(),
  organizer_id uuid NOT NULL REFERENCES events.organizers(id),
  title text NOT NULL,
  slug text NOT NULL,
  description text,
  category_id uuid REFERENCES events.categories(id),
  cover_url text,
  status text NOT NULL DEFAULT 'draft',
  created_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE (organizer_id, slug),
  CONSTRAINT ck_events_status CHECK (status IN ('draft','published','archived'))
);
```

**Tickets – `db/migration/tickets/V1__init.sql`**
```sql
CREATE TABLE IF NOT EXISTS tickets.ticket_types (
  id uuid PRIMARY KEY DEFAULT public.gen_random_uuid(),
  event_id uuid NOT NULL,
  name text NOT NULL,
  price numeric(12,2) NOT NULL,
  currency text NOT NULL DEFAULT 'ARS',
  capacity int NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS tickets.tickets (
  id uuid PRIMARY KEY DEFAULT public.gen_random_uuid(),
  ticket_type_id uuid NOT NULL REFERENCES tickets.ticket_types(id),
  code text NOT NULL UNIQUE,
  buyer_email text,
  status text NOT NULL DEFAULT 'available',
  sold_at timestamptz,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT ck_tickets_status CHECK (status IN ('available','sold','checked_in','refunded'))
);
```
