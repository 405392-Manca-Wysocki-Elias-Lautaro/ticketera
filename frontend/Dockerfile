# =========================
# Etapa de desarrollo (hot reload)
# =========================
FROM node:20-alpine AS dev
WORKDIR /app
ENV NODE_ENV=development
ENV NEXT_TELEMETRY_DISABLED=1
ENV CHOKIDAR_USEPOLLING=true
ENV WATCHPACK_POLLING=true

# Instalar pnpm
RUN npm install -g pnpm

# Exponer puertos
EXPOSE 3000

# Comando por defecto (se puede sobrescribir en docker-compose)
CMD ["sh", "-c", "if [ ! -d \"node_modules/next\" ]; then echo \"Node modules not found. Installing...\"; pnpm install --prefer-offline; fi && echo \"Starting dev server...\" && pnpm run dev"]

# =========================
# Etapa de build
# =========================
FROM node:20-alpine AS builder
WORKDIR /app
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

# Instalar pnpm
RUN npm install -g pnpm

# Copiar deps
COPY package.json pnpm-lock.yaml ./

# Instalar deps (incluye devDependencies que Next necesita para build)
RUN pnpm install --frozen-lockfile

# Copiar código completo
COPY . .

# ARG para la URL de la API (se pasa en build time)
ARG NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_URL=${NEXT_PUBLIC_API_URL}

# Ejecutar build
RUN pnpm run build


# =========================
# Etapa final (runtime)
# =========================
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

# Instalar pnpm
RUN npm install -g pnpm

# Copiar solo lo necesario para producción
COPY --from=builder /app/package.json ./package.json
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/next.config.js ./next.config.js
COPY --from=builder /app/node_modules ./node_modules

EXPOSE 3000
CMD ["pnpm", "start"]
