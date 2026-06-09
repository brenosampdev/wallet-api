# ---- Estágio de build ----
FROM gradle:8.10.2-jdk21 AS builder
USER root
WORKDIR /app

# Copia apenas os arquivos de configuração para aproveitar o cache de dependências
COPY build.gradle settings.gradle gradle.properties* ./
COPY gradle gradle

# Baixa as dependências (sem compilar)
RUN gradle dependencies --no-daemon || true

# Copia o código-fonte e faz o build
COPY src ./src
RUN gradle build -x test --no-daemon

# ---- Estágio de execução ----
FROM eclipse-temurin:21-jre
ARG JAVA_OPTS
WORKDIR /app

# Copia o JAR gerado (ajuste o nome se necessário)
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c","java ${JAVA_OPTS} -jar app.jar"]