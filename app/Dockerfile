FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]