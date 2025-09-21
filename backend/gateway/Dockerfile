# --- Imágenes base parametrizables ---
ARG JDK_IMAGE=eclipse-temurin:17-jdk
ARG RUNTIME_IMAGE=eclipse-temurin:17-jre

# ====== STAGE 1: Build ======
FROM ${JDK_IMAGE} AS build
WORKDIR /workspace

# Copia mínima para cachear dependencias
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

# Copiamos el resto del código y construimos
COPY src ./src
RUN ./mvnw -q -DskipTests package

# Guardamos la ruta del jar empacado
RUN JAR_FILE=$(ls target/*-SNAPSHOT.jar 2>/dev/null || ls target/*.jar) && \
    echo "$JAR_FILE" > /jar_path

# ====== STAGE 2: Runtime ======
FROM ${RUNTIME_IMAGE} AS runtime
WORKDIR /app

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/urandom" \
    SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080

COPY --from=build /jar_path /jar_path
RUN cp "$(cat /jar_path)" /app/app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health | grep '"status":"UP"' || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
