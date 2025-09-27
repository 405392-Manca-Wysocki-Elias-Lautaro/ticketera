# Ticketera – Environments & Configuration

Este proyecto es un **monorepo** con microservicios Java (Spring Boot) y un frontend en React (Vite).

```
/ticketera
  /frontend
    .env.example
  /backend
    /gateway
      .env.example
      application.yml
      application-dev.yml
      application-staging.yml
      application-prod.yml
    /auth
      .env.example
      application.yml
      application-*.yml
    /event
      .env.example
      application.yml
      application-*.yml
```

---

## 🔑 Variables de entorno (Secrets)

Los **secrets** son valores sensibles (ejemplo: contraseñas de DB, claves JWT, tokens de APIs) que **no deben versionarse**.  

- **Local:** en archivos `.env` (no comiteados).  
- **GitHub Actions (CI/CD):** en `Settings → Secrets and variables → Actions`.  
- **Producción:** como variables de entorno en el servidor (Docker Compose / Kubernetes).  

Siempre se comitean los `.env.example` como **plantilla**.

---

## 🖥️ Frontend (React + Vite)

- Las variables deben empezar con `VITE_`.  
- Se definen en `.env.local`, `.env.staging`, `.env.production`.  

Ejemplo (`.env.example`):

```ini
VITE_APP_NAME=Ticketera
VITE_API_BASE_URL=http://localhost:8080
VITE_AUTH_URL=http://localhost:8081
VITE_SENTRY_DSN=
VITE_FEATURE_FLAGS=debug,devtools
```

---

## 🔧 Backend (Spring Boot microservices)

Cada servicio (gateway, auth, event) tiene:

- `application.yml`: configuración común.  
- `application-dev.yml`: entorno de desarrollo.  
- `application-staging.yml`: entorno de staging.  
- `application-prod.yml`: entorno de producción.  

El entorno activo se define con la variable:

```bash
export SPRING_PROFILES_ACTIVE=dev
```

o en Docker Compose:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
```

---

## 🌐 Comunicación entre servicios

- **Frontend → Gateway** (único expuesto públicamente).  
- **Microservicios entre sí → Directo** vía red interna de Docker (`http://auth:8080`, `http://event:8080`).  
- El Gateway enruta peticiones externas hacia cada servicio.  

---

## 🚀 Cómo levantar localmente

1. Copiar variables de entorno:
   ```bash
   cp frontend/.env.example frontend/.env
   cp backend/auth/.env.example backend/auth/.env
   cp backend/event/.env.example backend/event/.env
   cp backend/gateway/.env.example backend/gateway/.env
   ```

2. Editar valores según tu entorno.

3. Levantar con Docker Compose:
   ```bash
   docker compose up --build
   ```

---

## 🔒 Secrets en CI/CD

En GitHub Actions se configuran en:

```
Settings → Secrets and variables → Actions
```

Ejemplos:
- `JWT_SECRET_STAGING`
- `DB_PASS_STAGING`
- `S3_SECRET_KEY_PROD`

Estos secrets se inyectan en los workflows y luego como variables de entorno en los contenedores.

---

## 📌 Resumen

- `.env.example` → plantilla comiteada (sin secretos).  
- `.env` → valores reales (local/server), ignorado por git.  
- `application.yml` → config común.  
- `application-<profile>.yml` → config específica por entorno.  
- `SPRING_PROFILES_ACTIVE` → decide qué configuración cargar.  
