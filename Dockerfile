# Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run (Render sets PORT; Spring reads it via application-prod.properties)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/Graduate_Project.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
