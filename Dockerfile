# Etapa 1: build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/TDSE_LAB_NO.06-1.0.0.jar app.jar

ENV APP_ENV=production
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]