FROM maven:3.9.5-eclipse-temurin-21-alpine AS build

WORKDIR /app/backend

COPY backend/pom.xml .

RUN mvn dependency:go-offline

COPY backend/src ./src

RUN mvn clean package \
-DskipTests \
-Dspotless.skip=true

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","app.jar"]
