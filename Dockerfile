# STAGE 1 — BUILD
# Use Maven + Java 17 to build the JAR file
# This stage exists only to compile — it won't be in the final image
FROM maven:3.9-eclipse-temurin-17 AS build

# set working directory inside the container
WORKDIR /app

# copy pom.xml first and download dependencies
# Docker caches this layer — if pom.xml doesn't change,
# dependencies won't be re-downloaded on every build
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copy source code and build the JAR
# -DskipTests because we run tests separately in CI/CD
COPY src ./src
RUN mvn clean package -DskipTests

# STAGE 2 — RUN
# Use only JRE (not full JDK) — smaller final image
# build stage is thrown away, only the JAR is kept
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# copy only the JAR from the build stage
# nothing else — no source code, no Maven, no JDK
COPY --from=build /app/target/*.jar app.jar

# expose the port your app runs on
EXPOSE 8081

# command to run when container starts
ENTRYPOINT ["java", "-jar", "app.jar"]