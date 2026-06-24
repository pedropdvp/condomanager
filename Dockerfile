# =====================================================================
# CondoManager — imagem de produção (multi-stage)
# Stack: Java 21 (Temurin) + Spring Boot 3 (ver docs/TECH_STACK.md)
# =====================================================================

# ---- Stage 1: build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Cache de dependências: copia primeiro o pom e resolve offline.
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline
COPY src ./src
# Empacota sem testes (a pipeline de CI corre os testes em separado).
RUN mvn -q -B -DskipTests clean package

# ---- Stage 2: runtime ----
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Utilizador sem privilégios (segurança).
RUN groupadd --system condo && useradd --system --gid condo --home /app condo

# Pasta dos documentos (filesystem local — ver SPEC.md item E).
RUN mkdir -p /app/data/documentos && chown -R condo:condo /app

COPY --from=build /app/target/condomanager-*.jar /app/condomanager.jar

ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    DOCUMENTOS_DIR=/app/data/documentos \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"

EXPOSE 8080
USER condo

# Healthcheck via endpoint público de saúde.
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD wget -qO- http://localhost:8080/api/v1/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/condomanager.jar"]
