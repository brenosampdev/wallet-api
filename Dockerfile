# ---- Estágio de build ----
FROM gradle:8.10.2-jdk21 AS builder
USER root
WORKDIR /app

ARG BUILD_MODE=prod

COPY build.gradle settings.gradle gradle.properties* ./
COPY gradle/ gradle/
COPY gradlew .
RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon || true

COPY src ./src
RUN ./gradlew build -x test --no-daemon

# ---- Estágio de execução ----
FROM eclipse-temurin:21-jre

ARG JAVA_OPTS=""
ENV JAVA_OPTS=${JAVA_OPTS}

WORKDIR /app
COPY --from=builder /app/build/libs/app.jar app.jar

EXPOSE 8000
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]