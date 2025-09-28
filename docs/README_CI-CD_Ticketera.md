# Ticketera · CI/CD & Workflows (Monorepo)
> **Objetivo:** documentar cómo funciona el pipeline de integración y release del monorepo `ticketera` (frontend en React + backend en Java/Spring), qué hace cada workflow, cómo versionamos con semantic-release, cómo publicamos imágenes en GHCR y cómo verificar todo con un smoke test.

---

## 📦 Estructura del repositorio

```
/ticketera
  /frontend                  # React
  /backend                   # Java / Spring
    /gateway
    /auth-service
    /event-service
    /ticket-service
    /payment-service
    /notification-service
.github/workflows/           # Workflows de GitHub Actions
commitlint.config.cjs        # Reglas de Conventional Commits
CODEOWNERS                   # Responsables por carpeta
PULL_REQUEST_TEMPLATE.md     # Template de PR
```

---

## 🌿 Estrategia de ramas

- `main` → rama estable (releases de producción). Llega **solo** por PR.
- `develop` → integración del sprint (**pre‑releases**).
- `feature/*` → trabajo diario de cada dev.
- `hotfix/*` → arreglos urgentes que salen a `main` y se backmergean a `develop`.

> No usamos `release/*` (con 2 devs es fricción extra).

---

## 🧾 Resumen de Workflows

| Archivo | ¿Cuándo corre? | ¿Qué valida / produce? |
|---|---|---|
| `ci-pr.yml` | En cada **PR** a `develop` o `main` | Detecta qué carpetas cambiaron, **build + test** del frontend y **verify** de cada microservicio Java tocado. |
| `docker-ci-pr.yml` | En cada **PR** a `develop` o `main` | **Construye imágenes Docker** solo de lo que cambió (no publica). |
| `commitlint.yml` | Al abrir/editar/sincronizar **PR** | Verifica **Conventional Commits** en el **título** y en los **commits**. |
| `release-develop.yml` | Push a **`develop`** | Corta **pre‑releases** (`-next`) por módulo (tags + changelog) y **publica imágenes** etiquetadas para testing en **GHCR**. |
| `release-main.yml` | Push a **`main`** | Corta **releases estables** (tags + changelog) y **publica imágenes** con `vX.Y.Z` y `latest` en **GHCR**. |
| `guard-branches.yml` | Push directo a `main` o `develop` | **Falla** si el push no viene de bot/release; recordatorio de usar PR. |

---

## 🧩 Detalle de cada workflow

### 1) `ci-pr.yml` — CI en Pull Requests
- **Trigger:** PR a `develop` o `main`.
- **Pasos clave:**
  1. `dorny/paths-filter` marca flags por carpeta (frontend, gateway, auth, …) para ejecutar **solo** lo que cambió.
  2. **Frontend (React):** Node.js v20.x + cache npm → `npm ci` → `npm run lint` → `npm test` (si existe) → `npm run build`.
  3. **Backend (Java/Spring):** JDK 17 (Temurin) + cache Maven → `mvn verify` (o `./mvnw verify`) **por servicio tocado**.
- **Objetivo:** el PR queda validado si el código compila, pasa tests y no rompe lo que fue modificado.

### 2) `docker-ci-pr.yml` — Build Docker en PR
- **Trigger:** PR a `develop` o `main`.
- **Pasos clave:**
  - Misma detección por carpetas/Dockerfile.
  - `docker/setup-buildx-action` + `docker/build-push-action@v6` para **construir** imágenes (sin push) con **cache de capas** (más rápido).
- **Objetivo:** atrapar errores de Dockerfile/dep. **antes** de mergear.

### 3) `commitlint.yml` — Convencional en PR
- **Trigger:** abrir/editar/sincronizar PR.
- **Pasos:** instala commitlint en el runner y valida **título** y **commits** del PR con `@commitlint/config-conventional` (regla `subject-case` relajada).
- **Objetivo:** mensajes consistentes para que semantic‑release infiera **bump** (patch/minor/major).

### 4) `release-develop.yml` — Pre‑releases en `develop`
- **Trigger:** push a `develop` (normalmente al mergear PR).
- **Pasos (por módulo/carpeta):**
  - **semantic‑release** con `branches: [{ name: 'develop', prerelease: 'next' }, 'main']`.
  - Genera **tag prerelease** (por módulo): p. ej. `gateway-v1.2.0-next.3` y actualiza `CHANGELOG.md`.
  - Construye y **publica** imágenes a **GHCR** con etiqueta de canal: `develop-next-<sha>` (listas para QA).
- **Objetivo:** tener artefactos listos para testing durante el sprint.

### 5) `release-main.yml` — Releases estables en `main`
- **Trigger:** push a `main` (merge desde `develop` o hotfix).
- **Pasos (por módulo/carpeta):**
  - **semantic‑release** corta versión **estable** (sin `-next`), actualiza changelog y crea `GitHub Release`.
  - Construye y **publica** imágenes a GHCR con **dos tags**: `vX.Y.Z` y `latest`.
- **Objetivo:** cierre prolijo del sprint y artefactos de producción versionados.

### 6) `guard-branches.yml` — Guardia anti push directo
- **Trigger:** push directo a `main`/`develop`.
- **Comporta:** permite **solo** commits de release o bots; cualquier otro **falla el check** con aviso “Usá PR”.
- **Objetivo:** disciplina de PR sin necesitar reglas de protección.

---

## 🏷️ Versionado por módulo con semantic‑release

Cada carpeta relevante tiene su **`release.config.cjs`**:
- **Frontend** → `tagFormat: "frontend-v${version}"`.
- **Gateway** → `tagFormat: "gateway-v${version}"`.
- **Auth** → `tagFormat: "auth-service-v${version}"`.
- …y así para cada microservicio.

**Plugins típicos:**
- `@semantic-release/commit-analyzer` + `@semantic-release/release-notes-generator`
- `@semantic-release/changelog` (escribe/actualiza `CHANGELOG.md`)
- `@semantic-release/git` (commitea changelog, y en frontend también `package.json` si corresponde)
- `@semantic-release/github` (crea el Release)
- **Frontend solo:** `@semantic-release/npm` con `{ npmPublish: false }` (no publica a npm; mantiene versión en `package.json`).

> **Nota:** en servicios Java no hay `package.json`, se usa changelog + tags + release de GitHub.

---

## 🐳 Imágenes en GHCR (GitHub Container Registry)

- Registry: `ghcr.io/<OWNER>/…`
- Nombres propuestos:
  - `ghcr.io/<OWNER>/ticketera-frontend`
  - `ghcr.io/<OWNER>/ticketera-gateway`
  - `ghcr.io/<OWNER>/ticketera-auth-service`
  - `ghcr.io/<OWNER>/ticketera-event-service`
  - `ghcr.io/<OWNER>/ticketera-ticket-service`
  - `ghcr.io/<OWNER>/ticketera-payment-service`
  - `ghcr.io/<OWNER>/ticketera-notification-service`

**Tags que generamos:**
- En `develop`: `develop-next-<sha>` (rápido de identificar para QA).
- En `main`: `vX.Y.Z` y `latest`.

> El login a GHCR usa `GITHUB_TOKEN` del runner; no hay que crear secretos.

---

## ⚙️ Archivos de configuración (repo raíz)

### `commitlint.config.cjs`
```js
export default {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'subject-case': [0, 'never'], // flexible con mayúsculas/casos
  },
};
```

### `CODEOWNERS` (ejemplo)
```
*                                        @usuario1 @usuario2
/ticketera/frontend/                     @usuario1
/ticketera/backend/auth-service/         @usuario2
/ticketera/backend/event-service/        @usuario2
/ticketera/backend/ticket-service/       @usuario1
/ticketera/backend/payment-service/      @usuario1
/ticketera/backend/notification-service/ @usuario2
/ticketera/backend/gateway/              @usuario1
```

### `PULL_REQUEST_TEMPLATE.md`
```md
## Resumen
- ¿Qué cambia y por qué?

## Checklist
- [ ] Commits con Conventional Commits
- [ ] CI verde (lint/test/build)
- [ ] Changelog (lo genera semantic-release)
```

---

## 🧱 Docker: pautas rápidas

- **`.dockerignore`** por módulo (evitar copiar `.git`, `node_modules`, `target`, etc.).
- **Multi‑stage**:
  - Frontend: build en `node:20` → servir con `nginx:alpine`.
  - Spring: build en `maven:3.9-eclipse-temurin-17` → run en `eclipse-temurin:17-jre`.
- **Labels OCI** y `--build-arg VCS_REF=$GITHUB_SHA` opcional para trazabilidad.
- **Cache de capas** activo en los workflows (rápidos).

---

## 🧰 Dependencias & secretos necesarios

- **Commitlint** y **semantic‑release** se instalan en los runners con `npx`. No necesitás preinstalar nada.
- **GHCR** usa el `GITHUB_TOKEN` interno (sin secretos extra).
- Si más adelante usás otro registry, agregás `DOCKER_USERNAME/DOCKER_PASSWORD` y cambiás `docker/login-action`.

---

## 🧯 Problemas comunes (y solución rápida)

- **Falla `commitlint`** → corregí el título/commits al formato `type(scope): subject` (p. ej. `feat(auth): login con JWT`).
- **Falla `ci-pr` en Java** → corré local `mvn -B verify` en ese servicio; faltan tests o dependencia.
- **Falla `docker-ci-pr`** → revisá el `Dockerfile` y `.dockerignore` de ese módulo.
- **No aparece la imagen en GHCR** → verificá pestaña **Packages** del repo y que corrió `release-*` (develop/main).
- **No se generan pre‑releases** → asegurate de mergear a `develop` y que haya **commits convencionales**.

---

## ✅ Smoke Test (paso a paso)

> Objetivo: verificar que todo funciona end‑to‑end.

### Pre‑chequeo
1. Confirmá que están estos archivos en el repo:
   - `.github/workflows/ci-pr.yml`
   - `.github/workflows/docker-ci-pr.yml`
   - `.github/workflows/commitlint.yml`
   - `.github/workflows/release-develop.yml`
   - `.github/workflows/release-main.yml`
   - `.github/workflows/guard-branches.yml`
   - `commitlint.config.cjs`
   - `release.config.cjs` en `ticketera/frontend` **y** en cada `ticketera/backend/*`
2. Revisá que haya **Dockerfile** en `ticketera/frontend` y en cada microservicio.
3. Acordate de los **nombres de imágenes** (GHCR) que usarán los workflows.

### Paso 1 — PR de prueba a `develop`
```bash
git checkout -b feature/smoke-test
# modificar un archivo simple en ticketera/frontend o en 1 microservicio
git add .
git commit -m "feat(frontend): prueba de pipeline (smoke test)"
git push -u origin feature/smoke-test
# Abrir PR contra develop
```
**Esperado en PR:**
- ✅ `ci-pr` (solo para lo modificado)
- ✅ `docker-ci-pr` (construye imagen del módulo modificado)
- ✅ `commitlint`

### Paso 2 — Merge a `develop`
- Al mergear, corre `release-develop`:
  - Verás **pre‑release** en **Releases** con tag por módulo (`*-vX.Y.Z-next.N`).
  - En **Packages** del repo aparecerá la **imagen** del módulo con tag `develop-next-<sha>`.

### Paso 3 — PR `develop` → `main` (cierre)
- Abrir PR y mergear.
- `release-main` debe:
  - Crear **Release estable** por módulo (`*-vX.Y.Z`).
  - Publicar imágenes en GHCR con tags `vX.Y.Z` y `latest`.
- Verificarlo en **Releases** y **Packages**.

### Paso 4 — Anti‑patrón (opcional)
- Intentar un **push directo** a `develop`:
  - El workflow `guard-branches` debe **fallar** con mensaje “Usá PR”.

### Paso 5 — Romper adrede (opcional)
- Modificar un `Dockerfile` con un error y levantar PR:
  - `docker-ci-pr` debe **fallar** (demuestra que la guardia funciona).
- Hacer un commit con mal formato (ej. `Update`):
  - `commitlint` debe **fallar** hasta que lo corrijas.

---

## 🙋 FAQ rápida

- **¿Qué es GHCR?** GitHub Container Registry: el “depósito” de imágenes Docker que integra con GitHub.
- **¿Necesito secrets?** No, usamos `GITHUB_TOKEN` por defecto.
- **¿Publica a npm?** No. En frontend usamos `@semantic-release/npm` solo para manejar versión/local changelog, **sin publicar**.
- **¿Puedo cambiar los tags de imágenes?** Sí, ajustá los `tags:` en los jobs de Docker en `release-*`.
- **¿Y si un servicio no debe publicar imagen?** Quitalo de la matriz de Docker o agregá una condición en `if:`.
ersión/local changelog, **sin publicar**.
- **¿Puedo cambiar los tags de imágenes?** Sí, ajustá los `tags:` en los jobs de Docker en `release-*`.
- **¿Y si un servicio no debe publicar imagen?** Quitalo de la matriz de Docker o agregá una condición en `if:`.