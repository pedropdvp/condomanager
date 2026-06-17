# ===== Etapa de build (Maven + JDK 21) =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache de dependencias
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline

# Codigo e empacotamento
COPY src ./src
RUN mvn -q -B -DskipTests clean package

# ===== Etapa de execucao (apenas JRE 21) =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/condomanager-1.0.0.jar app.jar

# Perfil de producao (MySQL) por omissao; sobreponivel por SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=prod
# A maioria dos PaaS injeta a porta via $PORT; o Spring usa server.port
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
