# Build (Vite)
ARG NODE_IMAGE=node:22-alpine
FROM ${NODE_IMAGE} AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build  # genera /app/dist

# Serve estático con Node (sin nginx)
FROM ${NODE_IMAGE} AS serve
WORKDIR /srv
RUN npm i -g serve
COPY --from=build /app/dist /srv/dist
EXPOSE 8081
# "serve" por defecto escucha 3000; le forzamos 8081 y host 0.0.0.0
CMD ["sh","-c","serve -s /srv/dist -l 8081 --no-clipboard"]
