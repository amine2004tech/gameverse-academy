# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the shaded JAR
COPY --from build /app/target/gameverseacademy-1.0-SNAPSHOT.war app.jar

# Copy webapp resources (as Main.java expects them on the filesystem)
COPY src/main/webapp /app/src/main/webapp
COPY data /app/data
COPY assets /app/assets

# Expose the port
EXPOSE 8080

# Run the application
# We set database.path if we want to use SQLite in the container
ENTRYPOINT ["java", "-Ddatabase.path=data/gameverse.db", "-jar", "app.jar"]
