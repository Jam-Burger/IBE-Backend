# Stage 1: Build
FROM --platform=linux/amd64 maven:3.9-amazoncorretto-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM --platform=linux/amd64 amazoncorretto:21-alpine
WORKDIR /app
RUN apk add --no-cache curl

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]