# ====== STAGE 1: BUILD (Java 17) ======
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache de dependencias
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Código y empaquetado
COPY src ./src
# Si aún no querés deps de test, salteá compilación de tests:
RUN mvn -q -Dmaven.test.skip=true package

# Copiá el jar a un nombre fijo para el siguiente stage
RUN JAR="$(ls target/*-SNAPSHOT.jar 2>/dev/null || ls target/*.jar | head -n 1)" \
 && test -n "$JAR" \
 && cp "$JAR" /workspace/app.jar

# ====== STAGE 2: RUNTIME (Java 17) ======
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/urandom" \
    SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080

# Copiá el jar ya normalizado
COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080

# (Opcional) requiere actuator
# HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
#   CMD wget -qO- http://localhost:8080/actuator/health | grep '"status":"UP"' || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
