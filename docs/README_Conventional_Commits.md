# Ticketera · Guía de Conventional Commits

> Esta guía explica **qué son los Conventional Commits**, cómo escribirlos, por qué importan (versionado automático con `semantic-release`) y cómo usarlos en el día a día del monorepo **/ticketera** (React + Spring). Incluye **ejemplos reales** con nuestros módulos.

---

## 🧠 ¿Qué es un Conventional Commit?
Es un **formato estándar** para los mensajes de commit. Permite que herramientas como **semantic-release** detecten automáticamente si un cambio es **patch**, **minor** o **major**, generen **CHANGELOG**, creen **tags** y, en nuestro caso, disparen **imágenes Docker** con esas versiones.

**Estructura:**

```
<tipo>(<scope>)< ! >: <resumen en imperativo, breve>
<línea en blanco>
<cuerpo opcional; explica el qué/por qué>
<línea en blanco>
<footer opcional: BREAKING CHANGE: … | Closes #123 | Refs #NNN>
```

- `tipo`: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`.
- `scope`: **opcional**, 1 palabra que indica el módulo/área. En monorepo, usamos **carpetas**:
  - `frontend`, `gateway`, `auth-service`, `event-service`, `ticket-service`, `payment-service`, `notification-service`.
- `!`: indica **cambio rompedor** (breaking change). Alternativa: usar `BREAKING CHANGE:` en el footer.
- `resumen`: en **imperativo** (ej.: “agrega”, “corrige”, “soporta”), en una sola línea y breve.
- `body/footer`: más contexto, referencias, `Closes #123`, etc.

---

## 🧩 Tipos de commit (y qué release disparan)

| Tipo        | ¿Para qué se usa?                                | Impacto de versión (por defecto `@semantic-release/commit-analyzer`) |
|-------------|---------------------------------------------------|------------------------------------------------------------------------|
| `feat`      | Nueva funcionalidad para el usuario               | **minor**                                                              |
| `fix`       | Corrección de bug                                 | **patch**                                                              |
| `perf`      | Mejora de rendimiento sin cambiar comportamiento  | **patch**                                                              |
| `refactor`  | Cambio interno sin features ni fixes              | _sin release_ (salvo que sea breaking)                                |
| `docs`      | Documentación                                     | _sin release_                                                          |
| `style`     | Formato, comas, espacios; sin cambios de lógica   | _sin release_                                                          |
| `test`      | Agregar/ajustar tests                             | _sin release_                                                          |
| `build`     | Cambios de build, dependencias, empaquetado       | _sin release_                                                          |
| `ci`        | Cambios en pipelines/Workflows CI/CD              | _sin release_                                                          |
| `chore`     | Mantenimiento general                             | _sin release_                                                          |
| `revert`    | Revertir un commit previo                         | **patch**                                                              |
| `BREAKING CHANGE` | Cambio incompatible (API/contrato)         | **major** (siempre, esté donde esté)                                   |

> **Nota:** Si un commit incluye `!` o un footer `BREAKING CHANGE: …`, genera **major**. `feat` => **minor**; `fix`/`perf`/`revert` => **patch**. Los demás no generan release por defecto.

---

## 🏷️ Scopes recomendados para Ticketera
Usamos nombres de carpeta/módulo como `scope`:

- `frontend`
- `gateway`
- `auth-service`
- `event-service`
- `ticket-service`
- `payment-service`
- `notification-service`
- `ci`, `docker`, `docs`, `build` (cuando aplique a esas áreas transversales)

Ejemplos de scopes compuestos (si hace falta): `ci(docker)`, `build(frontend)`.

---

## ✅ Ejemplos buenos (con y sin body)

### Feature en frontend
```
feat(frontend): agrega vista de detalle de evento

Permite ver line-up, horarios y entradas disponibles.
Closes #123
```

### Fix en gateway
```
fix(gateway): corrige propagación de header X-Request-ID

Se perdía en el filtro de seguridad y complicaba el tracing.
```

### Breaking change en auth-service (dos formas)
```
feat(auth-service)!: requiere refresh tokens para /login

BREAKING CHANGE: Se eliminó el campo `sessionId` del response de /login.
```
o
```
feat(auth-service): soporta refresh tokens en /login

BREAKING CHANGE: Se eliminó el campo `sessionId` del response de /login.
```

### Mejora de performance (patch)
```
perf(event-service): reduce a 1 query la carga de eventos por fecha
```

### Refactor sin impacto funcional
```
refactor(ticket-service): separa capa de repositorios del dominio
```

### Cambios de CI/CD
```
ci(docker): build de imágenes en PR con buildx y cache
```

### Documentación
```
docs(readme): agrega sección de smoke test de pipelines
```

### Revert
```
revert: feat(frontend): agrega vista de detalle de evento

Este revert vuelve al comportamiento anterior por bug en producción.
Refs #130
```

---

## ❌ Ejemplos malos (y cómo corregirlos)

| Incorrecto | Problema | Correcto |
|---|---|---|
| `Update` | No sigue el formato; no dice qué ni dónde | `docs(readme): actualiza sección de despliegue` |
| `Fix bug` | Sin scope ni detalle | `fix(payment-service): corrige cálculo de comisiones` |
| `Feature: Login nuevo` | Palabra “Feature” y mayúsculas en subject | `feat(auth-service): agrega endpoint de login` |
| `chore: arregla bug` | `chore` no es para bugs | `fix(ticket-service): evita NPE al crear ticket` |
| `feat: GRAN CAMBIO` | Título en UPPER/sentence case | `feat(frontend): agrega selector de butacas` |

**Reglas rápidas:**  
- Imperativo y breve (~50–72 caracteres en el subject).  
- Evitá punto final en el subject.  
- `scope` en minúsculas, una sola palabra.  
- Si es **rompedor**, usa `!` o `BREAKING CHANGE:`.  

---

## 🧪 Cómo comitear (CLI, Desktop, cuerpos y footers)

### CLI (git)
```bash
git add .
git commit -m "feat(frontend): agrega selector de butacas"             -m "Permite visualizar disponibilidad y precios por fila."             -m "Closes #321"
```

### GitHub Desktop
- Escribí el **Summary** como `tipo(scope): subject`.
- En **Description** poné cuerpo/footers (incluye `Closes #NNN` si cierra issues).

### Template local (opcional)
Podés crear `~/.gitmessage.txt` y configurar:
```ini
git config --global commit.template ~/.gitmessage.txt
```
Contenido sugerido:
```
<type>(<scope>): <subject>

<body opcional>

<BREAKING CHANGE: ...>
<Refs|Closes #id>
```

---

## 🔧 commitlint (repo)
Archivo `commitlint.config.cjs` en la raíz:
```js
export default {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'subject-case': [0, 'never'], // relajado con mayúsculas
  },
};
```
El workflow `commitlint.yml` valida **título del PR** y **commits**. Si falla, ajustá el mensaje.

---

## 🤖 ¿Cómo impacta en semantic-release?
- Lee los commits desde la última etiqueta:
  - `fix`/`perf`/`revert` ⇒ **patch** `x.y.(z+1)`
  - `feat` ⇒ **minor** `(y+1).0.0`
  - `BREAKING CHANGE` ⇒ **major** `(x+1).0.0`
- Genera **CHANGELOG.md**, **tags** por módulo (con nuestro `tagFormat`) y **GitHub Releases**.
- Nuestros workflows además construyen y suben **imágenes Docker** a GHCR con esas versiones.

---

## 🧭 Guía express (cheat sheet)

- **Nueva funcionalidad:** `feat(<servicio>): <qué>`  
- **Bugfix:** `fix(<servicio>): <qué>`  
- **Rendimiento:** `perf(<servicio>): <qué>`  
- **Cambio interno:** `refactor(<servicio>): <qué>`  
- **Docs/CI/Build/Test:** `docs|ci|build|test(<scope>): <qué>`  
- **Rompe compatibilidad:** agrega `!` y/o `BREAKING CHANGE:` en el footer.

**Scopes válidos:** `frontend | gateway | auth-service | event-service | ticket-service | payment-service | notification-service | ci | docker | docs | build`

---

## 🧱 Dudas frecuentes

- **¿Puedo escribir el subject en español?** Sí. Usá **imperativo**: “agrega”, “corrige”, “refactoriza”.
- **¿Tengo que usar siempre scope?** Recomendado en monorepo; ayuda a leer y a filtrar.
- **¿Qué pasa si toco varias carpetas?** Elegí el scope más representativo. Detallá el resto en el cuerpo.
- **¿Y si me olvido del formato?** `commitlint` te lo va a marcar en el PR; reescribí o squash con un título correcto.

---

Hecho para que tus commits cuenten una historia clara y automática 🚀
