# =========================
# Etapa base
# =========================
FROM node:20-alpine AS base
WORKDIR /app
RUN npm install -g pnpm
COPY package.json pnpm-lock.yaml ./

# =========================
# Etapa de desarrollo
# =========================
FROM base AS dev
ENV NODE_ENV=development
ENV NEXT_TELEMETRY_DISABLED=1

# 🔧 Evitar symlinks problemáticos
RUN pnpm config set node-linker hoisted

# Copiar el código fuente (sin node_modules, ignorado por .dockerignore)
COPY . .

# Instalar dependencias (ahora sí dentro del contenedor)
RUN pnpm install --frozen-lockfile

# Validar styled-jsx
RUN test -d node_modules/styled-jsx || (echo "❌ styled-jsx no se encuentra en node_modules" && exit 1)

EXPOSE 3000
CMD ["pnpm", "dev"]

# =========================
# Etapa de build (producción)
# =========================
FROM base AS builder
ENV NODE_ENV=production
RUN pnpm config set node-linker hoisted
RUN pnpm install --frozen-lockfile
COPY . .
RUN pnpm run build

# =========================
# Etapa final (runtime producción)
# =========================
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

RUN npm install -g pnpm
RUN pnpm config set node-linker hoisted

COPY --from=builder /app/package.json ./package.json
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/next.config.mjs ./next.config.mjs
COPY --from=builder /app/node_modules ./node_modules

EXPOSE 3000
CMD ["pnpm", "start"]
