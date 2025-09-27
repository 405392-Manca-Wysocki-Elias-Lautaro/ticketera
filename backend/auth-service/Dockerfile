# ===== deps (cache m2) =====
FROM maven:3.9.8-eclipse-temurin-17 AS deps
WORKDIR /workspace
# Solo el pom para resolver deps y cachear
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

# ===== dev (hot reload con spring-boot:run) =====
FROM maven:3.9.8-eclipse-temurin-17 AS dev
WORKDIR /workspace
ENV SPRING_PROFILES_ACTIVE=dev
EXPOSE 8080 5005
# Debug remoto (JDWP)
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
# En dev montás el código como volumen, acá no copiamos src
ENTRYPOINT ["sh","-c","exec mvn -q -Dspring-boot.run.profiles=${SPRING_PROFILES_ACTIVE:-dev} spring-boot:run"]

# ===== build (prod) =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests clean package

# ===== prod (runtime) =====
FROM eclipse-temurin:17-jre AS prod
WORKDIR /workspace
# Usuario no-root
RUN groupadd -r app && useradd -r -g app app
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/urandom"
# Copio el fat JAR generado por Spring Boot
COPY --from=build /workspace/target/*.jar /app/app.jar
USER app
EXPOSE 8080
# exec para que señales lleguen a la JVM
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
