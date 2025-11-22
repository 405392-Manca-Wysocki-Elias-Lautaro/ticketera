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
COPY --from=builder /app/next.config.mjs ./next.config.mjs
COPY --from=builder /app/node_modules ./node_modules

EXPOSE 3000
CMD ["pnpm", "start"]
