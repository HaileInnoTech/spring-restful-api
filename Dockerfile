# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# copy pom trước để cache dependency
COPY pom.xml .

RUN mvn dependency:go-offline

# copy source
COPY src ./src

# build jar
RUN mvn clean package -DskipTests


# ===== Run stage =====
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]